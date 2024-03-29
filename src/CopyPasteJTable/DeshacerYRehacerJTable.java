package CopyPasteJTable;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.undo.UndoManager;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.table.TableModel;

/**
 * Basic implementation of a 
 * @author Thierry LEFORT
 * 11 févr. 08
 *
 */
public class DeshacerYRehacerJTable extends UndoManager implements TableModel {
	/** Undo KeyStroke : Control + Z */
	public static final KeyStroke UNDO_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK);
 	/** Redo KeyStroke : Control + Y */
	public static final KeyStroke REDO_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK); 
	/**
	 * @see UIManager#getString(Object)
	 * @return the UIManager String for <code>"AbstractUndoableEdit.undoText"</code>
	 */
	public static final String getUndoText() {
		return UIManager.getString("AbstractUndoableEdit.undoText");
	}
	/**
	 * @see UIManager#getString(Object)
	 * @return the UIManager String for <code>"AbstractUndoableEdit.redoText"</code>
	 */
	public static final String getRedoText() {
		return UIManager.getString("AbstractUndoableEdit.redoText");
	}
	
	/**
	 * Install an UndoManager on a JTable
	 * @see #install(JTable, TableModel)
	 * @param table the table
	 * @return the Installed Manager
	 */
	public static UndoManager install(JTable table) {
		return install(table, table.getModel());
	}
	/**
	 * Install an UndoManager on the given table with the given model.
	 * @param table the table
	 * @param model the model
	 * @return the installed manager
	 */
	public static UndoManager install(JTable table, TableModel model) {
		final DeshacerYRehacerJTable manager = new DeshacerYRehacerJTable(model);
		/* UNDO */
		String undo = getUndoText();
		Action undoAction = new AbstractAction(undo) {
			public void actionPerformed(ActionEvent e) {
				if (manager.canUndo()) {
					manager.undo();
				}
			}
		};
		table.getActionMap().put(undo, undoAction);
		table.getInputMap().put(UNDO_KEY, undo);
		/* REDO */
		String redo = getRedoText();
		Action redoAction = new AbstractAction(redo) {
			public void actionPerformed(ActionEvent e) {
				if (manager.canRedo()) {
					manager.redo();
				}
			}
		};
		table.getActionMap().put(redo, redoAction);
		table.getInputMap().put(REDO_KEY, redo);
		
		table.setModel(manager);
		return manager;
	}
	/**
	 * Remove all Undo settings from the table 
	 * @param table the table
	 */
	public static void uninstall(JTable table) {
		table.getActionMap().remove(getUndoText());
		table.getActionMap().remove(getRedoText());
		table.getInputMap().remove(UNDO_KEY);
		table.getInputMap().remove(REDO_KEY);
		TableModel model = table.getModel();
		if (model instanceof DeshacerYRehacerJTable) {
			DeshacerYRehacerJTable undoModel = (DeshacerYRehacerJTable) model;
			table.setModel(undoModel._modelDelegate);
		}
	}
	 
	
	/**
	 * Model delegator
	 */
	private TableModel _modelDelegate;
	/**
	 * Default constructor 
	 * @param model delegator
	 */
	public DeshacerYRehacerJTable(TableModel model) {
		_modelDelegate = model;
	}
	/**
	 * {@link TableModel#addTableModelListener(TableModelListener)}
	 */
        @Override
	public void addTableModelListener(TableModelListener l) {
		_modelDelegate.addTableModelListener(l);
	}
	/**
	 * {@link TableModel#getColumnClass(int)}
	 */
	public Class<?> getColumnClass(int columnIndex) {
		return _modelDelegate.getColumnClass(columnIndex);
	}
	/**
	 * {@link TableModel#getColumnCount()}
	 */
	public int getColumnCount() {
		return _modelDelegate.getColumnCount();
	}
	/**
	 * {@link TableModel#getColumnName(int columnIndex)}
	 */
	public String getColumnName(int columnIndex) {
		return _modelDelegate.getColumnName(columnIndex);
	}
	/**
	 * {@link TableModel#getRowCount()}
	 */
	public int getRowCount() {
		return _modelDelegate.getRowCount();
	}
	/**
	 * {@link TableModel#getValueAt(int rowIndex, int columnIndex)}
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		return _modelDelegate.getValueAt(rowIndex, columnIndex);
	}
	/**
	 * {@link TableModel#isCellEditable(int rowIndex, int columnIndex)}
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return _modelDelegate.isCellEditable(rowIndex, columnIndex);
	}
	/**
	 * {@link TableModel#removeTableModelListener(TableModelListener l)}
	 */
	public void removeTableModelListener(TableModelListener l) {
		_modelDelegate.removeTableModelListener(l);
	}
	/**
	 * {@link TableModel#setValueAt(Object, int, int)}
	 * 
	 * Store the old and new value into a {link CellUndoableEdit} and add it,
	 * {@link UndoManager#addEdit(javax.swing.undo.UndoableEdit).
	 */
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		Object oldValue = getValueAt(rowIndex, columnIndex);
		_modelDelegate.setValueAt(value, rowIndex, columnIndex);
		//It is important to make the setValueAt before adding the edit in the UndoManager,
		//if you fail to set the value you don't want to add it. 
		CellUndoableEdit edit = 
			new CellUndoableEdit(
					_modelDelegate, 
					rowIndex, 
					columnIndex, 
					oldValue, 
					value);
		addEdit(edit);
		
	}
	
        class CellUndoableEdit extends AbstractUndoableEdit {
	/** row and column being edited */
	private int _row, _column;
	/** old and new value */
	private Object _oldValue, _newValue;
	/** Model being edited */
	private TableModel _model;
	
	/**
	 * 
	 * @param model model under edition
	 * @param row row being edited
	 * @param column column being edited
	 * @param oldValue previous value
	 * @param newValue new value
	 */
	public CellUndoableEdit(TableModel model, int row, int column, Object oldValue, Object newValue) {
		_model = model;
		_row = row;
		_column = column;
		_oldValue = oldValue;
		_newValue = newValue;
	}
	
	/**
	 * 
	 */
	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		_model.setValueAt(_newValue, _row, _column);
	}
	
	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		_model.setValueAt(_oldValue, _row, _column);
	}
	

}
}
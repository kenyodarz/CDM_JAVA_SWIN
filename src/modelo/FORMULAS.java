package modelo;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class FORMULAS {

    public static double HallarIpOIs(double kva, double voltaje, JComboBox combofase) {
        double Ip = 0;
        int fase = Integer.parseInt(combofase.getSelectedItem().toString());
        if (fase == 1) {
            Ip = (kva * 1000) / voltaje;
        } else if (fase == 3) {
            Ip = ((kva * 1000) / Math.sqrt(3)) / voltaje;
        }
        return QuitarDecimales(Ip, 2);
    }

    public static double HallarRn(double vp, double vs, int fase) {
        double Rn = 0;
        if (fase == 1) {
            Rn = vp / vs;
        } else if (fase == 3) {
            Rn = (vp * Math.sqrt(3)) / vs;
        }
        return FORMULAS.QuitarDecimales(Rn, 3);
    }

    public static double HallarRnMinMax(double rn, double por) {
        double Rn = 0;
        Rn = rn * por;
        return QuitarDecimales(Rn, 3);
    }

    public static double HallarPromedioR1(JTextField r1, JTextField r2, JTextField r3, JComboBox combofase, JTextField pro) {
        double promedio = 0;
        if (Integer.parseInt(combofase.getSelectedItem().toString()) == 1) {
            try {
                pro.setText(r1.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "El valor de la resistencia primaria en la FASE U (U-V OHM) esta mal escrita o esta vacia.");
            }
        } else {
            try {
                if (Double.parseDouble(r1.getText()) >= 0) {
                    try {
                        if (Double.parseDouble(r2.getText()) >= 0) {
                            try {
                                if (Double.parseDouble(r3.getText()) >= 0) {
                                    double rs1 = Double.parseDouble(r1.getText());
                                    double rs2 = Double.parseDouble(r2.getText());
                                    double rs3 = Double.parseDouble(r3.getText());
                                    promedio = QuitarDecimales((rs1 + rs2 + rs3) / 3, 2);
                                    pro.setText(String.valueOf(promedio));
                                }
                            } catch (NumberFormatException e) {
                                BordeRojo(r3);
                            }
                        }
                    } catch (NumberFormatException e) {
                        BordeRojo(r2);
                    }
                }
            } catch (NumberFormatException e) {
                BordeRojo(r1);
            }
        }
        return promedio;
    }

    public static void BordeRojo(JTextField t) {
        t.setBorder(new LineBorder(Color.red, 2));
    }

    public static int HallarTensionBT(JTextField t) {
        int tensionBT = 0;
        try {
            if (Integer.parseInt(t.getText()) >= 0 && !t.getText().isEmpty()) {
                tensionBT = Integer.parseInt(t.getText()) * 2;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error de escritura en el voltaje secundario\n" + e);
        }
        return tensionBT;
    }

    public static int HallarTiempodePrueba(JTextField t) {
        int tiempo = 0;
        try {
            if (Double.parseDouble(t.getText()) >= 0) {
                tiempo = (int) ((120 * 60) / Double.parseDouble(t.getText()));
            }
        } catch (NumberFormatException e) {
            t.setBorder(new LineBorder(Color.red, 2));
        }
        return tiempo;
    }

    public static double QuitarDecimales(double numerodecimal, int numerodecimales) {
        numerodecimal = numerodecimal * Math.pow(10, numerodecimales);
        numerodecimal = Math.round(numerodecimal);
        numerodecimal = numerodecimal / Math.pow(10, numerodecimales);
        return numerodecimal;
    }

    public static double HallarCorrienteEnI(JTextField Iu, JTextField Iv, JTextField Iw, JTextField Is, JComboBox combofase, JTextField pro) {
        double promedio = 0;

        try {
            if (Double.parseDouble(Is.getText()) >= 0) {
                if (Integer.parseInt(combofase.getSelectedItem().toString()) == 1) {
                    try {
                        double iu = Double.parseDouble(Iu.getText());
                        double is = Double.parseDouble(Is.getText());
                        promedio = QuitarDecimales((iu / is) * 100, 2);
                        pro.setText(String.valueOf(promedio));
                    } catch (NumberFormatException e) {
                        BordeRojo(Iu);
                    }
                } else if (Integer.parseInt(combofase.getSelectedItem().toString()) == 3) {
                    try {
                        if (Double.parseDouble(Iu.getText()) >= 0) {
                            try {
                                if (Double.parseDouble(Iv.getText()) >= 0) {
                                    try {
                                        if (Double.parseDouble(Iw.getText()) >= 0) {
                                            double iu = Double.parseDouble(Iu.getText());
                                            double iv = Double.parseDouble(Iv.getText());
                                            double iw = Double.parseDouble(Iw.getText());
                                            double is = Double.parseDouble(Is.getText());
                                            promedio = QuitarDecimales((((iu + iv + iw) / 3) / is) * 100, 2);
                                            pro.setText(String.valueOf(promedio));
                                        }
                                    } catch (NumberFormatException e) {
                                        BordeRojo(Iw);
                                    }
                                }
                            } catch (NumberFormatException e) {
                            }
                        }
                    } catch (NumberFormatException e) {
                        BordeRojo(Iu);
                    }
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "La corriente secundaria esta vacia(Is)\nNo se puede calcular I%");
            BordeRojo(Is);
        }
        return promedio;
    }

    public static double HallarGarantiza(JComboBox comboservicio, JComboBox combofase, int ano, double kva) {

        String servicio = comboservicio.getSelectedItem().toString();
        int fase = Integer.parseInt(combofase.getSelectedItem().toString());
        int año = ano;
        if (servicio.equalsIgnoreCase("NUEVO") || servicio.equalsIgnoreCase("RECONSTRUIDO")) {
            if (fase == 1) {
                if (año <= 1994) {
                    String sql = "SELECT * FROM monofasico WHERE kva<=" + kva + " ";
                    ConexionBD conex = new ConexionBD();
                    conex.conectar();
                    ResultSet rs = conex.CONSULTAR(sql);
                    try {
                        if (rs.next()) {

                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(FORMULAS.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        conex.CERRAR();
                    }
                }
            }
        }

        return 0;
    }

    public static double getKva(String tabla, double KVAdigitada){
        double kva = 0;
        String sql = "select * from " + tabla + " ORDER BY kva ASC";
        ConexionBD conex = new ConexionBD();
        conex.conectar();
        ResultSet rs = conex.CONSULTAR(sql);
        boolean esta = false;
        try {
            double BD, auxiliar = 0;
            while (rs.next()) {
                System.out.println(rs.getDouble(1));
                BD = rs.getDouble("kva");
                if (BD == KVAdigitada) {
                    kva = BD;
                    esta = true;
                    break;
                } else if (BD < KVAdigitada) {
                    auxiliar = BD;
                } else if(KVAdigitada < ((auxiliar + BD) / 2)) {
                    kva = auxiliar;
                    esta = true;
                    break;
                } else {
                    kva = BD;
                    esta = true;
                    break;
                }
            }
            if (!esta) {
                JOptionPane.showMessageDialog(null, "NO SE ENCONTRO LA POTENCIA DE " + KVAdigitada + " EN LA TABLA " + tabla);
            }
        } catch (SQLException ex) {
            Logger.getLogger(FORMULAS.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Error al buscar el kva mas adecuado\n" + ex);
        } finally {
            conex.CERRAR();
        }
        return kva;
    }

    public static double[] getValores(JTextField cjpotencia, JComboBox combofasetransformador, JComboBox comboservicio, JFormattedTextField cjfechafabricacion, String estado, JTextField cjiogarantizado, JTextField cjpogarantizado, JTextField cjpccgarantizado, JTextField cjuzgarantizado, JTextField cjtensionprimaria, JComboBox comborefrigeracion, JComboBox comboclasedeaislamiento, JLabel lblmostrarpcu, JTextField cjcliente) {
        double[] valores = new double[4];
//        try {
        try {
            try {
                String tabla = null;
                double ano = 0;
                if (comboservicio.getSelectedItem().toString().equalsIgnoreCase("NUEVO") || comboservicio.getSelectedItem().toString().equalsIgnoreCase("RECONSTRUIDO")) {
                    String[] a = cjfechafabricacion.getText().split("/");
                    ano = Double.parseDouble(a[1]);
                } else {
                    ano = Double.parseDouble(cjfechafabricacion.getText());
                }
                if(comboservicio.getSelectedItem().toString().equalsIgnoreCase("NUEVO") || comboservicio.getSelectedItem().toString().equalsIgnoreCase("RECONSTRUIDO")) {
                    double vp = Double.parseDouble(cjtensionprimaria.getText());
                    if (combofasetransformador.getSelectedItem().toString().equalsIgnoreCase("1")) {
                        if (vp <= 15000) {
                            tabla = "monofasiconuevo";
                        } else if (vp > 15000 && vp <= 35000) {
                            tabla = "monofasiconuevoserie35";
                        }
                    }else if (combofasetransformador.getSelectedItem().toString().equalsIgnoreCase("3")){
                        if (vp <= 15000) {
                            tabla = "trifasiconuevo";
                        } else if (vp > 15000 && vp <= 35000) {
                            tabla = "trifasiconuevoserie35";
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "NO SE PUDO ENCONTRAR LA TABLA PARA HALLAR LOS VALORES GARANTIZADOS (Io),(Uz),(Po),(Pcc)");
                    }
                } else if (comboservicio.getSelectedItem().toString().equalsIgnoreCase("REPARADO")) {
//                tabla = (año<1996)?Integer.parseInt(combofasetransformador.getSelectedItem().toString()) == 1?"monofasicoantesde1996":"trifasicoantesde1996":(Integer.parseInt(combofasetransformador.getSelectedItem().toString()) == 1)?"monofasicodespuesde1996":"trifasicodespuesde1996";
                    double vp = Double.parseDouble(cjtensionprimaria.getText());
                    if (combofasetransformador.getSelectedItem().toString().equalsIgnoreCase("1")) {
                        if (ano < 1996) {
                            if (vp <= 15000) {
                                tabla = "monofasicoantesde1996";
                            } else if (vp > 15000 && vp <= 35000) {
                                tabla = "monofasicoantesde1996serie35";
                            }
                        } else if (ano >= 1996) {
                            if (vp <= 15000) {
                                tabla = "monofasicodespuesde1996";
                            } else if (vp > 15000 && vp <= 35000) {
                                tabla = "monofasicodespuesde1996serie35";
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "NO SE PUDO ENCONTRAR LA TABLA PARA HALLAR LOS VALORES GARANTIZADOS (Io),(Uz),(Po),(Pcc)");
                        }
                    } else if (combofasetransformador.getSelectedItem().toString().equalsIgnoreCase("3")) {
                        if (vp > 35000 && vp <= 46000) {
                            tabla = "trifasicodespuesde1996serie46";
                        } else if (ano < 1996) {
                            if (vp <= 15000) {
                                tabla = "trifasicoantesde1996";
                            } else if (vp > 15000 && vp <= 35000) {
                                tabla = "trifasicoantesde1996serie35";
                            }
                        } else if (ano >= 1996) {
                            if (vp <= 15000) {
                                tabla = "trifasicodespuesde1996";
                            } else if (vp > 15000 && vp <= 35000) {
                                tabla = "trifasicodespuesde1996serie46";
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "NO SE PUDO ENCONTRAR LA TABLA PARA HALLAR LOS VALORES GARANTIZADOS (Io),(Uz),(Po),(Pcc)");
                        }
                    }
                } else if (comboservicio.getSelectedItem().toString().equalsIgnoreCase("MANTENIMIENTO")) {
                    try {
                        if (estado.equalsIgnoreCase("ORIGINAL")) {
                            if (Integer.parseInt(combofasetransformador.getSelectedItem().toString()) == 1) {
                                tabla = "monofasiconuevo";
                            } else if (Integer.parseInt(combofasetransformador.getSelectedItem().toString()) == 3) {
                                tabla = "trifasiconuevo";
                            } else {
                                JOptionPane.showMessageDialog(null, "NO SE PUDO ENCONTRAR LA TABLA PARA HALLAR LOS VALORES GARANTIZADOS (Io),(Uz),(Po),(Pcc)");
                            }
                        } else if (estado.equalsIgnoreCase("REPARADO")) {
                            if (ano < 1996) {
                                if (Integer.parseInt(combofasetransformador.getSelectedItem().toString()) == 1) {
                                    tabla = "monofasicoantesde1996";
                                } else if (Integer.parseInt(combofasetransformador.getSelectedItem().toString()) == 3) {
                                    tabla = "trifasicoantesde1996";
                                } else {
                                    JOptionPane.showMessageDialog(null, "NO SE PUDO ENCONTRAR LA TABLA PARA HALLAR LOS VALORES GARANTIZADOS (Io),(Uz),(Po),(Pcc)");
                                }
                            } else if (ano >= 1996) {
                                if (Integer.parseInt(combofasetransformador.getSelectedItem().toString()) == 1) {
                                    tabla = "monofasicodespuesde1996";
                                } else if (Integer.parseInt(combofasetransformador.getSelectedItem().toString()) == 3) {
                                    tabla = "trifasicodespuesde1996";
                                } else {
                                    JOptionPane.showMessageDialog(null, "NO SE PUDO ENCONTRAR LA TABLA PARA HALLAR LOS VALORES GARANTIZADOS (Io),(Uz),(Po),(Pcc)");
                                }
                            }
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "EL TIPO DE TRANSFORMAFDOR NO HA SIDO SELECCIONADO - ORIGINAL ó REPARADO\n" + e);
                    }
                }
                if (cjcliente.getText().equals("EMPRESAS PUBLICAS DE MEDELLIN S.A E.S.P")) {
                    tabla = (Integer.parseInt(combofasetransformador.getSelectedItem().toString()) == 1) ? "monofasiconuevo" : "trifasiconuevo";
                }
                double KVA = 0;
//        Locale spanish = new Locale("es", "ES");
//        NumberFormat nf = NumberFormat.getInstance(spanish);
                if (!cjpotencia.getText().isEmpty()) {
                    KVA = Double.parseDouble(cjpotencia.getText().replace(",", "."));
                    //KVA = nf.parse(cjpotencia.getText()).doubleValue();        
                }
                if (comborefrigeracion.getSelectedIndex() == 2) {
                    if (combofasetransformador.getSelectedItem().toString().equalsIgnoreCase("3")){
                        double vp = Double.parseDouble(cjtensionprimaria.getText());
                        if (vp > 1200 && vp <= 15000) {
                            tabla = "trifasicosecoserie1512";
                        } else if (vp <= 1200) {
                            tabla = "trifasicosecoserie1212";
                        } else {
                            JOptionPane.showMessageDialog(null, "EL VOLTAJE ESCRITO ESTA FUERA DE RANGO SEGUN LAS CONDICIONES ESTABLECIAS\nNO SE PODRA CALCULAR LAS PERDIDAS");
                            tabla = null;
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "NO EXISTEN VALORES PARA TRANSFORMADORES MONOFASICOS SECOS\n");
                        tabla = null;
                    }
                }
                if (tabla == null) {
                    JOptionPane.showMessageDialog(null, "SI ESTAS VIENDO ESTE MENSAJE ES UNICA Y EXCLUSIVAMENTE PORQUE NO SE SELECCIONO NINGUNA TABLA DE LAS NORMAS PARA HALLAR LAS PERDIDAS\nVERIFIQUE QUE LA POTENCIA, EL VOLTAJE PRIMARIA, EL AÑO, Y EL SERVICIO SEAN LOS CORRECTOS.");
                }
                double kva = getKva(tabla, KVA);
                String sql = "SELECT * FROM " + tabla + " WHERE kva=" + kva;
                ConexionBD conex = new ConexionBD();
                conex.conectar();
                ResultSet rs = conex.CONSULTAR(sql);
                try {
                    if (rs.next()) {
                        if (comborefrigeracion.getSelectedIndex() == 2) {
                            cjiogarantizado.setText("" + rs.getDouble("io"));
                            cjpogarantizado.setText("" + rs.getDouble("po"));
                            cjuzgarantizado.setText("" + rs.getDouble("uz"));
                            switch (comboclasedeaislamiento.getSelectedIndex()) {
                                case 0:
                                    cjpccgarantizado.setText("" + rs.getDouble("pc75"));
                                    lblmostrarpcu.setText("Pcu a 75º");
                                    break;
                                case 1:
                                    cjpccgarantizado.setText("" + rs.getDouble("pc85"));
                                    lblmostrarpcu.setText("Pcu a 85º");
                                    break;
                                case 2:
                                    cjpccgarantizado.setText("" + rs.getDouble("pc100"));
                                    lblmostrarpcu.setText("Pcu a 100º");
                                    break;
                                case 3:
                                    cjpccgarantizado.setText("" + rs.getDouble("pc120"));
                                    lblmostrarpcu.setText("Pcu a 120º");
                                    break;
                                case 4:
                                    cjpccgarantizado.setText("" + rs.getDouble("pc145"));
                                    lblmostrarpcu.setText("Pcu a 145º");
                                    break;
                            }
                        } else {
                            lblmostrarpcu.setText("Pcu a 85º");
                            valores[0] = rs.getDouble(2);
                            valores[1] = rs.getDouble(3);
                            valores[2] = rs.getDouble(4);
                            valores[3] = rs.getDouble(5);
                            cjiogarantizado.setText("" + rs.getDouble("io"));
                            cjpogarantizado.setText("" + rs.getDouble("po"));
                            cjpccgarantizado.setText("" + rs.getDouble("pc"));
                            cjuzgarantizado.setText("" + rs.getDouble("uz"));
                        }
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(FORMULAS.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(null, "Error al buscar los valores\n" + ex);
                } finally {
                    conex.CERRAR();
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                JOptionPane.showMessageDialog(null, "El formato de fecha que estas ingresando no es valido para servicios NUEVO o RECONSTRUIDO\n" + e);
                Logger.getLogger(FORMULAS.class.getName()).log(Level.SEVERE, null, e);
                cjfechafabricacion.grabFocus();
            }
        } catch (NumberFormatException ex) {
        }
//        } catch (java.text.ParseException ex) {Logger.getLogger(FORMULAS.class.getName()).log(Level.SEVERE, null, ex);cjpotencia.grabFocus();BordeRojo(cjpotencia);}        
        return valores;
    }

    public static double HallarI2r(JComboBox combofasetransformador, JTextField cjcorrienteprimaria, JTextField cjcorrientesecundaria, JTextField cjpromedioresistenciaprimaria, JTextField cjpromedioresistenciasecundaria, JTextField cji2r) {
        double I2r = 0;

        try {
            if (Double.parseDouble(cjcorrienteprimaria.getText()) >= 0) {
                try {
                    if (Double.parseDouble(cjcorrientesecundaria.getText()) >= 0) {
                        try {
                            if (Double.parseDouble(cjpromedioresistenciaprimaria.getText()) >= 0) {
                                try {
                                    if (Double.parseDouble(cjpromedioresistenciasecundaria.getText()) >= 0) {

                                        double Ip = Double.parseDouble(cjcorrienteprimaria.getText());
                                        double Is = Double.parseDouble(cjcorrientesecundaria.getText());
                                        double Rp = Double.parseDouble(cjpromedioresistenciaprimaria.getText());
                                        double Rs = Double.parseDouble(cjpromedioresistenciasecundaria.getText());
                                        if (Integer.parseInt(combofasetransformador.getSelectedItem().toString()) == 1) {
                                            I2r = QuitarDecimales((Math.pow(Ip, 2) * Rp) + (Math.pow(Is, 2) * (Rs / 1000)), 1);
                                            cji2r.setText(String.valueOf(I2r));
                                        } else if (Integer.parseInt(combofasetransformador.getSelectedItem().toString()) == 3) {
                                            I2r = QuitarDecimales( 1.5 * ((Math.pow(Ip, 2) * Rp) + (Math.pow(Is, 2) * (Rs / 1000))), 2);
                                            cji2r.setText(String.valueOf(I2r));
                                        }
                                    }
                                } catch (NumberFormatException e) {
                                    BordeRojo(cjpromedioresistenciasecundaria);
                                }
                            }
                        } catch (NumberFormatException e) {
                            BordeRojo(cjpromedioresistenciaprimaria);
                        }
                    }
                } catch (NumberFormatException e) {
                    BordeRojo(cjcorrientesecundaria);
                }
            }
        } catch (NumberFormatException e) {
        }

        return I2r;
    }

    public static double HallarKc(JComboBox combomaterialconductor) {
        double k = 0;
        if (combomaterialconductor.getSelectedItem().toString().equalsIgnoreCase("COBRE")) {
            k = 234.5;
        } else {
            k = 225;
        }
        return k;
    }

    public static double HallarPcu85(JTextField cjperdidasdecobre, JTextField cji2r, double K, double I2R85, JTextField cjpcureferidoa85) {
        double pcu85 = 0;
        try {
            if (!cjperdidasdecobre.getText().isEmpty() && Double.parseDouble(cjperdidasdecobre.getText()) >= 0) {
                try {
                    if (!cji2r.getText().isEmpty() && Double.parseDouble(cji2r.getText()) >= 0) {

                        double pcu = Double.parseDouble(cjperdidasdecobre.getText());
                        double i2r = Double.parseDouble(cji2r.getText());
                        pcu85 = QuitarDecimales(((pcu - i2r) / K) + I2R85, 1);
                        cjpcureferidoa85.setText(String.valueOf(pcu85));
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "El valor de I2r esta vacio o mal escrito\n" + e);
                    cji2r.grabFocus();
                    BordeRojo(cji2r);
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El valor en las perdidas de cobre esta vacio o mal escrito\n" + e);
            cjperdidasdecobre.grabFocus();
            BordeRojo(cjperdidasdecobre);
        }
        return pcu85;
    }

}

package aplicarFiltros;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.border.LineBorder;

import objeto.Objeto;
import objeto.Rasgo;
import objeto.RasgoObjeto;

public class JListPanel extends JList {

 public JListPanel() {
   setCellRenderer(new CustomCellRenderer());
   }

 public static void main(String[] args) {
   JFrame frame = new JFrame();
   JPanel panel = new JPanel();
   Vector vector = new Vector();
   panel.setForeground(Color.black);
   panel.setBackground(Color.white);
   /*
   // first line
   JPanel jp1 = new JPanel(new FlowLayout(FlowLayout.LEFT));   // NEW
   jp1.add(new JLabel(new ImageIcon("gumby.gif")));
   jp1.add(new JLabel("A line for Gumby"));
   jp1.add(new JLabel(new ImageIcon("gumby2.gif")));

   // second line
   JPanel jp2 = new JPanel(new FlowLayout(FlowLayout.LEFT));  // NEW
   jp2.add(new JLabel(new ImageIcon("gumby.gif")));
   jp2.add(new JLabel("Another line for Gumby"));
   jp2.add(new JLabel(new ImageIcon("gumby2.gif")));
   */
   String path = "image/";
   File directorio = new File(path);
   String [] ficheros = directorio.list();
   for (int i = 0; i < ficheros.length; i++) {
	   if (ficheros[i].endsWith(".tif")){
		   Objeto o = new Objeto();
		   o.setPathImage(path+ficheros[i]);
		   o.setName(ficheros[i]);
		   List<RasgoObjeto> rasgos = new ArrayList<RasgoObjeto>();
		   rasgos.add(new RasgoObjeto(new Rasgo("AREA"),1.0));
		   o.setRasgos(rasgos);
		   ObjetoPanel jp = new ObjetoPanel(o);
		   vector.add(jp);
	   }
   }

   JListPanel jlwi = new JListPanel();
   jlwi.setListData(vector);
   jlwi.setBorder(new LineBorder(Color.black));
   
   panel.add(jlwi);
   
   frame.getContentPane().add(new JScrollPane(panel),BorderLayout.CENTER);
   //frame.getContentPane().add(panel);
   
   //frame.getContentPane().add(new JLabel(new ImageIcon("Lighthouse.jpg")));
   frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   frame.setSize(500,500);
   frame.setVisible(true);
   }

 class CustomCellRenderer implements ListCellRenderer {
   public Component getListCellRendererComponent
    (JList list, Object value, int index,
     boolean isSelected,boolean cellHasFocus) {
     Component component = (Component)value;
     component.setBackground
      (isSelected ? Color.DARK_GRAY : Color.white);
     component.setForeground
      (isSelected ? Color.white : Color.black);
     return component;
     }
   }
}

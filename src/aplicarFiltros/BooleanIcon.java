package aplicarFiltros;

import java.awt.*;
import java.net.*;
import javax.swing.*;
import javax.swing.table.*;

class BooleanIcon implements Icon {
	private static final Icon ICON_X;
	private static final Icon ICON_OK;
	static {
		String location = "http://www.oracle.com/technology/tech/blaf/specs/icons/statusindicators/";
		try {
			ICON_X = new ImageIcon(new URL("http://icons.iconarchive.com/icons/dryicons/simplistica/48/delete-icon.png"));
			ICON_OK = new ImageIcon(new URL("http://www.casaycarros.com/images/ok.gif"));
		} catch (MalformedURLException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public int getIconWidth() {
		return ICON_X.getIconWidth();
	}

	public int getIconHeight() {
		return ICON_X.getIconHeight();
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		if (c instanceof JCheckBox) {
			JCheckBox check = (JCheckBox) c;
			(check.isSelected() ? ICON_OK : ICON_X).paintIcon(c, g, x, y);
		}
	}
}


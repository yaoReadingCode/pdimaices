package aplicarFiltros.configuracion;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;

public class RenderTabla extends DefaultTableCellRenderer {
	
	private int errorRow = -1;
	private int errorCol = -1;
	private int antErrorRow = -1;
	private int antErrorCol = -1;
	
	public RenderTabla() {
		super();
	}


	public int getErrorRow() {
		return errorRow;
	}


	public void setErrorRow(int errorRow) {
		this.errorRow = errorRow;
	}


	public int getErrorCol() {
		return errorCol;
	}


	public void setErrorCol(int errorRow, int errorCol) {
		this.antErrorCol = this.errorCol;
		this.antErrorRow = this.errorRow;
		this.errorCol = errorCol;
		this.errorRow = errorRow;
	}
	
	public void resetErrors(){
		this.errorCol = -1;
		this.errorRow = -1;
		this.antErrorCol = -1;
		this.antErrorRow = -1;
	}
	

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		super.getTableCellRendererComponent (table, value, isSelected, hasFocus, row, column);
		if (value != null) 
			this.setText(value.toString());
		this.setBorder(new LineBorder(Color.gray,0,true));
		
		if (antErrorRow == row && antErrorCol == column){
			this.setBorder(new LineBorder(Color.gray,0,true));
		}


		if (errorRow == row && errorCol == column){
			this.setBorder(new LineBorder(Color.red,1,true));
		}

		return this;
	}


	public void cleanError() {
		this.antErrorCol = -1;
		this.antErrorRow = -1;
		this.errorCol = -1;
		this.errorRow = -1;
	}

}

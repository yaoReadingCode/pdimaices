package aplicarFiltros.configuracion;

import java.awt.GridBagLayout;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;

import java.awt.Component;
import java.awt.Font;
import java.awt.SystemColor;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.GridBagConstraints;
import java.util.List;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import aplicarFiltros.configuracion.exception.ValidationException;
import aplicarFiltros.configuracion.modelmapper.RasgoMapper;

import dataAcces.ObjectDao;

import objeto.Rasgo;
import java.awt.FlowLayout;
import javax.swing.JButton;
import java.awt.Dimension;
import java.awt.ComponentOrientation;

import javax.swing.CellEditor;
import javax.swing.CellRendererPane;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.SwingConstants;
import java.awt.Color;

public class AdminPanel extends JPanel {
	
	private static String labelError = "ERROR";

	private static final long serialVersionUID = 1L;
	private JPanel contenidoPanel = null;
	private JScrollPane dataScrollPane = null;
	private JTable dataTable = null;  //  @jve:decl-index=0:visual-constraint="452,101"
	private String panelTitle = "";  //  @jve:decl-index=0:
	private JPanel buttonsPanel = null;
	private JButton agregarButton = null;
	private JButton guardarButton = null;
	private JButton eliminarButton = null;
	private JScrollPane centerScrollPane = null;
	private JPanel footerPanel = null;
	private JPanel errorPanel = null;
	private JLabel errorLabel = null;
	private JLabel messageLabel = null;
	private RenderTabla tableRenderer = new RenderTabla();
	
	/**
	 * This is the default constructor
	 */
	public AdminPanel(GeneralTableModel model, String title) {
		super();
		this.panelTitle = title;
		initialize();
		model.setAdminPanel(this);
		getDataTable().setModel(model);
		getDataTable().setDefaultRenderer(Object.class, tableRenderer);
		getDataTable().setRowSelectionAllowed(true);
		getDataTable().setColumnSelectionAllowed(true);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.setLayout(new BorderLayout());
		this.add(getContenidoPanel(), BorderLayout.CENTER);
	}

	/**
	 * This method initializes ContenidoPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getContenidoPanel() {
		if (contenidoPanel == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.weighty = 1.0;
			gridBagConstraints1.gridx = 0;
			contenidoPanel = new JPanel();
			contenidoPanel.setLayout(new BorderLayout());
			contenidoPanel.setBorder(BorderFactory.createTitledBorder(null, this.panelTitle, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), SystemColor.textHighlight));
			contenidoPanel.add(getCenterScrollPane(), BorderLayout.CENTER);
			contenidoPanel.add(getFooterPanel(), BorderLayout.SOUTH);
		}
		return contenidoPanel;
	}

	/**
	 * This method initializes RasgosScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getDataScrollPane() {
		if (dataScrollPane == null) {
			dataScrollPane = new JScrollPane(getDataTable());
			dataScrollPane.setBorder(null);
			dataScrollPane.setPreferredSize(new Dimension(450, 150));
		}
		return dataScrollPane;
	}

	/**
	 * This method initializes RasgosTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getDataTable() {
		if (dataTable == null) {
			dataTable = new JTable();
		}
		return dataTable;
	}

	/**
	 * This method initializes buttonsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getButtonsPanel() {
		if (buttonsPanel == null) {
			buttonsPanel = new JPanel();
			buttonsPanel.setLayout(new FlowLayout());
			buttonsPanel.add(getAgregarButton(), null);
			buttonsPanel.add(getEliminarButton(), null);
			buttonsPanel.add(getGuardarButton(), null);
		}
		return buttonsPanel;
	}

	/**
	 * This method initializes agregarButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAgregarButton() {
		if (agregarButton == null) {
			agregarButton = new JButton();
			agregarButton.setText("Nuevo");
			agregarButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					GeneralTableModel tableModel = (GeneralTableModel) getDataTable().getModel();
					tableModel.addRow();
					int selectionIndex = tableModel.getRowCount() - 1;
					selectCell(selectionIndex, 0);
					getDataTable().revalidate();
					
				}
			});
		}
		return agregarButton;
	}

	/**
	 * This method initializes guardarButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getGuardarButton() {
		if (guardarButton == null) {
			guardarButton = new JButton();
			guardarButton.setText("Guardar");
			guardarButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try{
						GeneralTableModel tableModel = (GeneralTableModel) getDataTable().getModel();
						int row = getDataTable().getSelectedRow();
						tableModel.saveRow(row);
						selectCell(row, 0);
						getDataTable().revalidate();
						//getDataTable().repaint();
						
					}
					catch (ValidationException ex) {
						showErrorMessage(ex, getDataTable().getSelectedRow());
					}
					catch (Exception ex) {
						showErrorMessage(labelError,ex);
					}
				}

			});
		}
		return guardarButton;
	}

	/**
	 * This method initializes eliminarButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getEliminarButton() {
		if (eliminarButton == null) {
			eliminarButton = new JButton();
			eliminarButton.setText("Eliminar");
			eliminarButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					GeneralTableModel tableModel = (GeneralTableModel) getDataTable().getModel();
					int row = getDataTable().getSelectedRow();
					tableModel.deleteRow(row);
					getDataTable().revalidate();
					if (row -1 > -1)
						selectCell(row - 1, 0);
				}
			});
		}
		return eliminarButton;
	}

	/**
	 * This method initializes centerScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getCenterScrollPane() {
		if (centerScrollPane == null) {
			centerScrollPane = new JScrollPane();
			centerScrollPane.setPreferredSize(new Dimension(500, 200));
			centerScrollPane.setViewportView(getDataScrollPane());
		}
		return centerScrollPane;
	}

	/**
	 * This method initializes footerPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getFooterPanel() {
		if (footerPanel == null) {
			footerPanel = new JPanel();
			footerPanel.setLayout(new BoxLayout(getFooterPanel(), BoxLayout.Y_AXIS));
			footerPanel.add(getButtonsPanel(), null);
			footerPanel.add(getErrorPanel(), null);
		}
		return footerPanel;
	}

	/**
	 * This method initializes errorPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getErrorPanel() {
		if (errorPanel == null) {
			messageLabel = new JLabel();
			messageLabel.setText("");
			messageLabel.setForeground(Color.red);
			messageLabel.setHorizontalAlignment(SwingConstants.LEADING);
			errorLabel = new JLabel();
			errorLabel.setText("ERROR");
			errorLabel.setHorizontalTextPosition(SwingConstants.LEADING);
			errorLabel.setFont(new Font("Dialog", Font.BOLD, 12));
			errorLabel.setForeground(Color.red);
			errorLabel.setHorizontalAlignment(SwingConstants.LEFT);
			errorLabel.setVisible(false);
			errorPanel = new JPanel();
			errorPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			errorPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			errorPanel.add(errorLabel, null);
			errorPanel.add(messageLabel, null);
		}
		return errorPanel;
	}

	private void showErrorMessage(String textLabel,Exception ex) {
		errorLabel.setText(textLabel);
		errorLabel.setVisible(true);
		messageLabel.setText(ex.getMessage());
		
	}

	public void showErrorMessage(ValidationException ex, int row) {
		showErrorMessage("*",ex);
		final int errorRow = row;
		final int errorCol = ex.getFieldId();
		this.tableRenderer.setErrorCol(errorRow,errorCol);
		selectCell(errorRow, errorCol);
		getDataTable().revalidate();
		//this.repaint();
	}

	public void cleanErrorMessage() {
		this.tableRenderer.cleanError();
		this.errorLabel.setVisible(false);
		this.messageLabel.setText("");
		
	}

	public void selectCell(int row, int col) {
		getDataTable().getSelectionModel().setSelectionInterval(row, row);
		getDataTable().setColumnSelectionInterval(col, col);
		getDataTable().transferFocus();
		//getDataTable().repaint();
	}

}

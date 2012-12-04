package org.irmacard.cardmanagement;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JFrame;


import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JToolBar;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.net.URI;


import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;

import javax.swing.ListCellRenderer;

import org.irmacard.credentials.util.LogEntry;
import org.irmacard.credentials.util.LogEntry.Action;
import org.irmacard.credentials.idemix.IdemixCredentials;
import org.irmacard.credentials.info.CredentialDescription;
import org.irmacard.credentials.info.DescriptionStore;
import org.irmacard.credentials.info.InfoException;
import org.irmacard.credentials.BaseCredentials;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.scuba.smartcards.CardService;


public class MainWindow implements CredentialSelector {
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("org.irmacard.cardmanagement.messages"); //$NON-NLS-1$

	private JFrame frmCardManagement;
	/**
	 * @wbp.nonvisual location=742,219
	 */
	private final CredentialDetailView credentialDetailView = new CredentialDetailView();
	/**
	 * @wbp.nonvisual location=749,309
	 */
	private LogDetailView logDetailView;

	private BaseCredentials baseCredentials;

	private DefaultListModel credListModel;

	private JSplitPane splitPaneVert;

	private DefaultListModel logListModel;

	private DescriptionStore descriptions;

	/**
	 * Create the application.
	 * @param cardService 
	 */
	public MainWindow(CardService cardService) {
		this.baseCredentials = new IdemixCredentials(cardService);
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initialize() {
		frmCardManagement = new JFrame();
		frmCardManagement.setTitle(BUNDLE.getString("MainWindow.frmCardManagement.title")); //$NON-NLS-1$
		frmCardManagement.setBounds(100, 100, 690, 456);
		frmCardManagement.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmCardManagement.setExtendedState(frmCardManagement.getExtendedState()|JFrame.MAXIMIZED_BOTH);
		
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		frmCardManagement.getContentPane().add(toolBar, BorderLayout.NORTH);
		
		JButton btnChangePin = new JButton(BUNDLE.getString("MainWindow.btnChangePin.text")); //$NON-NLS-1$
		toolBar.add(btnChangePin);
		
		splitPaneVert = new JSplitPane();
		splitPaneVert.setResizeWeight(0.6);
		splitPaneVert.setOrientation(JSplitPane.VERTICAL_SPLIT);
		frmCardManagement.getContentPane().add(splitPaneVert, BorderLayout.CENTER);
		
		JSplitPane splitPaneHoriz = new JSplitPane();
		splitPaneHoriz.setResizeWeight(0.5);
		splitPaneVert.setLeftComponent(splitPaneHoriz);
		
		JScrollPane scrollPaneLog = new JScrollPane();
		splitPaneHoriz.setRightComponent(scrollPaneLog);
		
		final JList listLog = new JList();
		listLog.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				if(!evt.getValueIsAdjusting()){
					selectLogIndex(listLog.getSelectedIndex());
				}
			}
		});
		listLog.addFocusListener(new FocusListener(){
			@Override
			public void focusGained(FocusEvent arg0) {
				selectLogIndex(listLog.getSelectedIndex());
			}

			@Override
			public void focusLost(FocusEvent arg0) {}
		});
		listLog.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPaneLog.setViewportView(listLog);
		logListModel = new DefaultListModel();
		List<LogEntry> logEntries = baseCredentials.getLog();
		for(LogEntry entry : logEntries) {
			logListModel.addElement(entry);
		}
		listLog.setModel(logListModel);
		listLog.setCellRenderer(new ListCellRenderer() {

			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected, boolean cellHasFocus) {
				LogEntry entry = (LogEntry)value;
				CredentialDescription credential = descriptions.getCredentialDescription(entry.getCredential());
				String string = entry.getTimestamp().toString() + ": " + (entry.getAction() == Action.ISSUE ? "Issue " : "Verify ") + credential.getName(); 
				
				return renderCell(string, isSelected, list);
			}
			
		});
		
		JScrollPane scrollPaneCredentials = new JScrollPane();
		splitPaneHoriz.setLeftComponent(scrollPaneCredentials);
		
		final JList listCredentials = new JList();
		listCredentials.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				selectCredentialIndex(listCredentials.getSelectedIndex());
			}
		});
		listCredentials.addFocusListener(new FocusListener(){
			@Override
			public void focusGained(FocusEvent arg0) {
				selectCredentialIndex(listCredentials.getSelectedIndex());
			}

			@Override
			public void focusLost(FocusEvent arg0) {}
		});
		listCredentials.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPaneCredentials.setViewportView(listCredentials);
		credListModel = new DefaultListModel();
		List<Integer> credentials = baseCredentials.getCredentials();
		try {
			URI core = new File(System.getProperty("user.dir")).toURI().resolve("irma_configuration/");
			DescriptionStore.setCoreLocation(core);
			descriptions = DescriptionStore.getInstance();
			for(Integer cred : credentials) {
				credListModel.addElement(descriptions.getCredentialDescription(cred.shortValue()));
			}
		} catch (InfoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		listCredentials.setModel(credListModel);
		listCredentials.setCellRenderer(new ListCellRenderer(){
			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected, boolean cellHasFocus) {
				CredentialDescription credential = (CredentialDescription)value;
				return renderCell(credential.getName(), isSelected, list);
			}
		});
		
		JLabel lblNothingSelected = new JLabel(BUNDLE.getString("MainWindow.lblNothinSelected.text"));
		splitPaneVert.setRightComponent(lblNothingSelected);
		
		logDetailView = new LogDetailView(baseCredentials, this);
	}

	private JLabel renderCell(String text, boolean isSelected, JList list) {
		JLabel label = new JLabel(text);
        if (isSelected) {
            label.setBackground(list.getSelectionBackground());
            label.setForeground(list.getSelectionForeground());
        } else {
        	label.setBackground(list.getBackground());
        	label.setForeground(list.getForeground());
        }
        label.setEnabled(list.isEnabled());
        label.setFont(list.getFont());
        label.setOpaque(true);
        return label;
	}
	
	/**
	 * Shows the log with the given index in the logDetailView
	 * @param index
	 */
	private void selectLogIndex(int index) {
		logDetailView.setLogEntry((LogEntry) logListModel.getElementAt(index));
		splitPaneVert.setRightComponent(logDetailView);
	}
	
	/**
	 * Shows the credential with the given index in the credentialDetailView
	 * @param index
	 */
	private void selectCredentialIndex(int index) {
		//short id = ((Integer)credListModel.getElementAt(index)).shortValue();
		CredentialDescription credential = (CredentialDescription)credListModel.getElementAt(index);
		selectCredential(credential);
	}
	
	/**
	 * Shows the credential with the given id in the credentialDetailView
	 * @param id
	 */
	public void selectCredential(CredentialDescription credential) {
		credentialDetailView.setCredential(credential, baseCredentials.getAttributes(credential.getId()));
		splitPaneVert.setRightComponent(credentialDetailView);
	}
	
	public void show() {
		frmCardManagement.setVisible(true);
	}

}

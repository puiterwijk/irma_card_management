package org.irmacard.cardmanagement;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.irmacard.credentials.Attributes;
import org.irmacard.credentials.info.AttributeDescription;
import org.irmacard.credentials.info.CredentialDescription;
import java.awt.BorderLayout;

public class CredentialDetailView extends JPanel {
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("org.irmacard.cardmanagement.messages"); //$NON-NLS-1$
	private static final long serialVersionUID = 1625933969087435098L;
	private JLabel lblCredName;
	private JLabel lblCredDescription;
	private JLabel lblIssuer;
	private JLabel lblIssuerLabel;
	private JPanel attributesPanel;
	private JLabel lblLock;
	private AttributeView attributeView_3;
	private AttributeView attributeView_4;
	private JButton btnLock;
	private JLabel noCredentialLabel;
	private JPanel credentialDetailPanel;
	
	/**
	 * Create the panel.
	 */
	public CredentialDetailView() {
		setLayout(new BorderLayout(0, 0));
		credentialDetailPanel = new JPanel();
		
		SpringLayout springLayout = new SpringLayout();
		credentialDetailPanel.setLayout(springLayout);
		
		lblCredName = new JLabel();
		springLayout.putConstraint(SpringLayout.NORTH, lblCredName, 10, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, lblCredName, 10, SpringLayout.WEST, this);
		lblCredName.setForeground(new Color(0, 66, 137));
		lblCredName.setFont(new Font("Ubuntu", Font.BOLD, 14));
		credentialDetailPanel.add(lblCredName);
		
		attributesPanel = new JPanel();
		credentialDetailPanel.add(attributesPanel);
		
		lblCredDescription = new JLabel();
		springLayout.putConstraint(SpringLayout.WEST, lblCredDescription, 0, SpringLayout.WEST, lblCredName);
		lblCredDescription.setForeground(new Color(0, 66, 137));
		lblCredDescription.setFont(new Font("Ubuntu", Font.PLAIN, 11));
		springLayout.putConstraint(SpringLayout.NORTH, lblCredDescription, 0, SpringLayout.SOUTH, lblCredName);
		credentialDetailPanel.add(lblCredDescription);
	
		lblIssuerLabel = new JLabel();
		springLayout.putConstraint(SpringLayout.NORTH, attributesPanel, 10, SpringLayout.SOUTH, lblIssuerLabel);
		springLayout.putConstraint(SpringLayout.WEST, attributesPanel, 0, SpringLayout.WEST, lblIssuerLabel);
		springLayout.putConstraint(SpringLayout.WEST, lblIssuerLabel, 0, SpringLayout.WEST, lblCredDescription);
		attributesPanel.setLayout(new BoxLayout(attributesPanel, BoxLayout.Y_AXIS));
		
		attributeView_3 = new AttributeView();
		attributeView_3.setAlignmentX(Component.LEFT_ALIGNMENT);
		attributesPanel.add(attributeView_3);
		
		attributeView_4 = new AttributeView();
		attributeView_4.setAlignmentX(Component.LEFT_ALIGNMENT);
		attributesPanel.add(attributeView_4);
		
		lblIssuerLabel.setForeground(new Color(0, 66, 137));
		lblIssuerLabel.setFont(new Font("Ubuntu", Font.PLAIN, 11));
		lblIssuerLabel.setText(BUNDLE.getString("CredentialDetailView.lblIssuerLabel.text"));
		springLayout.putConstraint(SpringLayout.NORTH, lblIssuerLabel, 0, SpringLayout.SOUTH, lblCredDescription);
		credentialDetailPanel.add(lblIssuerLabel);
		
		lblIssuer = new JLabel();
		lblIssuer.setForeground(new Color(0, 66, 137));
		lblIssuer.setFont(new Font("Ubuntu", Font.PLAIN, 11));
		springLayout.putConstraint(SpringLayout.BASELINE, lblIssuer, 0, SpringLayout.BASELINE, lblIssuerLabel);
		springLayout.putConstraint(SpringLayout.WEST, lblIssuer, 0, SpringLayout.EAST, lblIssuerLabel);
		credentialDetailPanel.add(lblIssuer);
		
		btnLock = new JButton("");
		springLayout.putConstraint(SpringLayout.WEST, btnLock, 100, SpringLayout.EAST, lblCredDescription);
		springLayout.putConstraint(SpringLayout.SOUTH, btnLock, 0, SpringLayout.SOUTH, lblCredDescription);
		btnLock.setIcon(new ImageIcon(CredentialDetailView.class.getResource("/img/lock.png")));
		credentialDetailPanel.add(btnLock);
		
		lblLock = new JLabel(BUNDLE.getString("CredentialDetailView.lblNewLabel.text")); //$NON-NLS-1$
		springLayout.putConstraint(SpringLayout.NORTH, lblLock, 5, SpringLayout.NORTH, btnLock);
		lblLock.setForeground(new Color(0, 66, 137));
		lblLock.setFont(new Font("Ubuntu", Font.PLAIN, 11));
		springLayout.putConstraint(SpringLayout.WEST, lblLock, 6, SpringLayout.EAST, btnLock);
		credentialDetailPanel.add(lblLock);
		
		credentialDetailPanel.setVisible(false);
		add(credentialDetailPanel, BorderLayout.CENTER);
		
		noCredentialLabel = new JLabel(BUNDLE.getString("CredentialDetailView.noCredentialLabel.text"));
		noCredentialLabel.setFont(new Font("Ubuntu", Font.BOLD, 11));
		add(noCredentialLabel, BorderLayout.NORTH);
	}

	public void setCredential(CredentialDescription credential, Attributes attributes) {
		if(credential != null) {
			if(credential.getName() == "Student") {
				btnLock.setIcon(new ImageIcon(CredentialDetailView.class.getResource("/img/lock.png")));
				lblLock.setText(BUNDLE.getString("CredentialDetailView.lblNewLabel.text"));
			}
			else {
				btnLock.setIcon(new ImageIcon(CredentialDetailView.class.getResource("/img/lock_unlocked.png")));
				lblLock.setText(BUNDLE.getString("CredentialDetailView.lblNewLabel.text.unLocked"));
			}
			lblCredName.setText(credential.getName().toUpperCase());
			lblCredDescription.setText(credential.getDescription());
			lblIssuer.setText(credential.getIssuerID());
			attributesPanel.removeAll();
			for(AttributeDescription attribute : credential.getAttributes()) {
				byte[] value = attributes.get(attribute.getName());
				if(value != null) {
					AttributeView attributeView = new AttributeView();
					attributeView.setAttribute(attribute, new String());
					attributeView.setAlignmentX(Component.LEFT_ALIGNMENT);
					attributesPanel.add(attributeView);
				}
				else {
					System.out.println("Attribute " + attribute.getName() + " not found.");
				}
			}
			noCredentialLabel.setVisible(false);
			credentialDetailPanel.setVisible(true);
		}
		else {
			noCredentialLabel.setVisible(true);
			credentialDetailPanel.setVisible(false);
		}
	}
}

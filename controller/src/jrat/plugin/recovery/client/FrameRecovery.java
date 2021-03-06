package jrat.plugin.recovery.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import jrat.api.Client;
import jrat.api.Icons;
import jrat.api.ui.DefaultJTable;
import jrat.api.ui.DefaultJTableCellRenderer;

@SuppressWarnings("serial")
public class FrameRecovery extends JFrame {
	
	public static FrameRecovery INSTANCE;

	private List<Client> clients;
	
	private JPanel contentPane;
	private JTable table;
	private DefaultTableModel model;
	private JPopupMenu popupMenu;

	public FrameRecovery(List<Client> c) {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				INSTANCE = null;
			}
		});
		INSTANCE = this;
		this.clients = c;
		setIconImage(Icons.getIcon("Password Recovery", "/icons/icon.png").getImage());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 535, 370);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		model = new DefaultTableModel();
		model.addColumn("Program");
		model.addColumn("Client");
		model.addColumn("Username");
		model.addColumn("Password");
		model.addColumn("");

		table = new DefaultJTable();
		table.setModel(model);
		table.setRowHeight(25);
		table.setDefaultRenderer(Object.class, new DefaultJTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				
				if (column == 0) {
					label.setIcon(Icons.getIcon("Password Recovery", "/icons/" + label.getText() + ".png"));
				} else {
					label.setIcon(null);
				}
				
				return label;
			}
		});
		scrollPane.setViewportView(table);
		
		popupMenu = new JPopupMenu();
		
		JMenuItem mntmReload = new JMenuItem("Reload");
		mntmReload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (Client c : clients) {
					try {
						c.addToSendQueue(new Packet130GetEntries(c));
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		popupMenu.add(mntmReload);
		
		addPopup(scrollPane, popupMenu);
		addPopup(table, popupMenu);
	}
	
	public void addEntry(Client client, String program, String[] data) {
		String[] data1 = new String[data.length + 2];
		
		data1[0] = program;
		data1[1] = client.getIP();
		
		for (int i = 0; i < data.length; i++) {
			data1[i + 2] = data[i];
		}
		
		model.addRow(data1);
	}
	
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}

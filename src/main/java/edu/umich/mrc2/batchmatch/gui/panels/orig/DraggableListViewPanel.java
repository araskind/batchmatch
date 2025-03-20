package edu.umich.mrc2.batchmatch.gui.panels.orig;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.TransferHandler;

import edu.umich.mrc2.batchmatch.gui.LayoutGrid;
import edu.umich.mrc2.batchmatch.gui.LayoutItem;
import edu.umich.mrc2.batchmatch.gui.LayoutUtils;
import edu.umich.mrc2.batchmatch.gui.WhiteYellowCellRenderer;

public abstract class DraggableListViewPanel extends JPanel {

	private boolean enableDrag = true;
	private boolean centerText = false;

	private DefaultListModel<String> strings;
	// private ArrayList<String> fileNames;

	public DraggableListViewPanel() {

	}

	public DraggableListViewPanel(boolean enableDrag) {
		this.enableDrag = enableDrag;
	}

	public DraggableListViewPanel(boolean enableDrag, boolean centerText) {
		this.enableDrag = enableDrag;
		this.centerText = centerText;
	}

	public void refreshList(ArrayList<String> names) {
		invalidate();
		strings.clear();
		if (names != null)
			for (int i = 0; i < names.size(); i++) {
				strings.addElement(names.get(i));
			}

		revalidate();
		repaint();
	}

	public void setupPanel(ArrayList<String> names) {

		strings = new DefaultListModel<String>();

		if (names != null)
			for (int i = 0; i < names.size(); i++) {
				strings.addElement(names.get(i));
			}

		final JList<String> dndList = new JList<String>(strings);
		dndList.setDragEnabled(enableDrag);
		dndList.setDropMode(DropMode.INSERT);
		dndList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		dndList.setTransferHandler(new TransferHandler() {
			private int index;
			private boolean beforeIndex = false; // Start with `false` therefore if it is removed from or added to the
													// list it still works

			@Override
			public int getSourceActions(JComponent comp) {
				return MOVE;
			}

			@Override
			public Transferable createTransferable(JComponent comp) {
				// StringSelection s;
				index = dndList.getSelectedIndex();
				return new StringSelection(dndList.getSelectedValue());
			}

			@Override
			public void exportDone(JComponent comp, Transferable trans, int action) {
				if (action == MOVE) {
					if (beforeIndex)
						strings.remove(index + 1);
					else
						strings.remove(index);
				}
				// if (action == MOVE) {
				updateForNewOrdering(strings, index);
				// }
			}

			@Override
			public boolean canImport(TransferHandler.TransferSupport support) {
				return support.isDataFlavorSupported(DataFlavor.stringFlavor);
			}

			@Override
			public boolean importData(TransferHandler.TransferSupport support) {
				try {
					String s = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
					JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
					strings.add(dl.getIndex(), s);
					beforeIndex = dl.getIndex() < index ? true : false;
					return true;
				} catch (UnsupportedFlavorException | IOException e) {
					e.printStackTrace();
				}

				updateForNewOrdering(strings, index);
				return false;
			}
		});

		if (!enableDrag) {
			dndList.setOpaque(false);

			WhiteYellowCellRenderer ren = new WhiteYellowCellRenderer();
			ren.setOpaque(false);
			dndList.setCellRenderer(ren); // new WhiteYellowCellRenderer() );
			dndList.setBackground(new Color(255, 0, 0, 0));
			dndList.setForeground(Color.green);
			Font f = dndList.getFont().deriveFont(Font.BOLD);
			dndList.setFont(f);
		}
		if (centerText) {
			DefaultListCellRenderer renderer = (DefaultListCellRenderer) dndList.getCellRenderer();
			renderer.setHorizontalAlignment(JLabel.CENTER);
		}

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(dndList);
		dndList.setLayoutOrientation(JList.VERTICAL);

		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setMaximumSize(new Dimension(1000, 150));
		scrollPane.setPreferredSize(new Dimension(0, 150));

		if (!enableDrag) {
			scrollPane.setBackground(Color.WHITE);
			scrollPane.setForeground(Color.WHITE);
		}
		LayoutGrid panelGrid = new LayoutGrid();
		panelGrid.addRow(Arrays.asList(new LayoutItem(scrollPane, .99)));

		LayoutUtils.doGridLayout(this, panelGrid);
	}

	// Auto
	protected boolean reportOnNewOrdering(DefaultListModel<String> strings, Integer newSelection) {
		System.out.println("\nShuffle done on index" + newSelection);
		if (strings != null)
			for (int i = 0; i < strings.getSize(); i++) {
				System.out.println(strings.get(i));
			}
		return true;
	}

	public ArrayList<String> getStrings() {

		ArrayList<String> strStrings = new ArrayList<String>();
		for (int i = 0; i < strings.getSize(); i++)
			strStrings.add(strings.get(i));

		return strStrings;
	}

	protected abstract boolean updateForNewOrdering(DefaultListModel<String> strings, Integer newSelection);
}

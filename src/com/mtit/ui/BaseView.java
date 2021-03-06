package com.mtit.ui;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;

import com.mtit.process.SyncException;
import com.mtit.utils.FileDownload;

public class BaseView extends Composite {

	public BaseView(Composite parent) {
		super(parent, SWT.None);
		
		try {
			FileDownload.downloadGroupFile();
		} catch (SyncException e) {
			e.printStackTrace();
		}
	}
	
	protected TableViewerColumn createTableViewerColumn(String title,
			int bound, final int colNumber, TableViewer viewer) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer,
				SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

	/**
	 * Class to allow for column sorting.
	 * 
	 * @author Mei
	 * 
	 */
	protected static abstract class ColumnViewerSorter extends ViewerComparator {
		public static final int ASC = 1;

		public static final int NONE = 0;

		public static final int DESC = -1;

		private int direction = 0;

		private TableViewerColumn column;

		private ColumnViewer viewer;

		public ColumnViewerSorter(ColumnViewer viewer, TableViewerColumn column) {
			this.column = column;
			this.viewer = viewer;
			this.column.getColumn().addSelectionListener(
					new SelectionAdapter() {

						public void widgetSelected(SelectionEvent e) {
							if (ColumnViewerSorter.this.viewer.getComparator() != null) {
								if (ColumnViewerSorter.this.viewer
										.getComparator() == ColumnViewerSorter.this) {
									int tdirection = ColumnViewerSorter.this.direction;

									if (tdirection == ASC) {
										setSorter(ColumnViewerSorter.this, DESC);
									} else if (tdirection == DESC) {
										setSorter(ColumnViewerSorter.this, NONE);
									}
								} else {
									setSorter(ColumnViewerSorter.this, ASC);
								}
							} else {
								setSorter(ColumnViewerSorter.this, ASC);
							}
						}
					});
		}

		public void setSorter(ColumnViewerSorter sorter, int direction) {
			if (direction == NONE) {
				column.getColumn().getParent().setSortColumn(null);
				column.getColumn().getParent().setSortDirection(SWT.NONE);
				viewer.setComparator(null);
			} else {
				column.getColumn().getParent()
						.setSortColumn(column.getColumn());
				sorter.direction = direction;

				if (direction == ASC) {
					column.getColumn().getParent().setSortDirection(SWT.DOWN);
				} else {
					column.getColumn().getParent().setSortDirection(SWT.UP);
				}

				if (viewer.getComparator() == sorter) {
					viewer.refresh();
				} else {
					viewer.setComparator(sorter);
				}

			}
		}

		public int compare(Viewer viewer, Object e1, Object e2) {
			return direction * doCompare(viewer, e1, e2);
		}

		protected abstract int doCompare(Viewer viewer, Object e1, Object e2);
	}

}

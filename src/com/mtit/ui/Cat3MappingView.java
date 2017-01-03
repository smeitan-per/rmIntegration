package com.mtit.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.mtit.businessRules.CategoryMappingType;

import com.hexapixel.articles.paperclips.core.GridPrinter;
import com.hexapixel.articles.paperclips.test.PrintHandler;
import com.hexapixel.articles.paperclips.test.TableWrapper;
import com.mtit.entity.WebSiteCategoryDao;
import com.mtit.model.Category3MappingModelProvider;
import com.mtit.process.SyncException;
import com.mtit.utils.WebWidgetsUtils;

public class Cat3MappingView extends BaseView {

	private TableViewer viewer;
	private Table table;
	private Composite composite;
	private Composite parent;

	public Cat3MappingView(Composite parent) {
		super(parent);
		this.parent = parent;
	}

	public Composite createTabFolder() {
		composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(new GridLayout(3, false));

		table = new Table(composite, SWT.FULL_SELECTION | SWT.BORDER);

		createViewer(table);
		createButtons(composite);

		return composite;
	}

	private void createButtons(Composite child) {
		Button editButton = new Button(composite, SWT.PUSH);
		editButton.setText("Edit");
		editButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL));

		editButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection iselection = (IStructuredSelection) viewer
						.getSelection();
				CategoryMappingType mapType = (CategoryMappingType) iselection
						.getFirstElement();

				WizardDialog wizardDialog = new WizardDialog(parent.getShell(),
						new Cat3MappingWizard("EDIT", mapType));
				if (wizardDialog.open() == Window.OK) {
					viewer.refresh();
				} else {
				}
			}
		});

		Button printButton = new Button(composite, SWT.PUSH);
		printButton.setText("Print");
		printButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL));

		printButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				GridPrinter gp = new GridPrinter(new PrintHandler());
				gp.printTable(new TableWrapper(table), false, true, "Print Category3", composite.getShell());

			}

		});

		Button refreshButton = new Button(composite, SWT.PUSH);
		refreshButton.setText("Refresh");
		refreshButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL));

		refreshButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				try {
					WebSiteCategoryDao.refresh();
					viewer.setInput(Category3MappingModelProvider.INSTANCE.refreshList());
					viewer.refresh();
					
					MessageBox mb = new MessageBox(parent.getShell());
					mb.setMessage("Refresh completed ");
					mb.open();
										
				} catch (SyncException e) {
					IStatus status = new Status(IStatus.ERROR,"Group File download error","Group File download Error",e);
					ErrorDialog.openError(parent.getShell(), "Group File download Error Message Dialog", 
							"Encountered error while downloading", status);
				}
			}
		});
	}

	protected void createViewer(Table table) {
		viewer = new TableViewer(table);
		createColumns(viewer);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setInput(Category3MappingModelProvider.INSTANCE.getTitleMappings());

		// // Layout the viewer
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 5;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		viewer.getControl().setLayoutData(gridData);
	}

	// This will create the columns for the table
	private void createColumns(final TableViewer viewer) {
		String[] titles = { "MYOB Category 3", "Website Group", "Website Group ID",
				"Field Mapped To"};
		int[] bounds = { 200, 200, 150, 150 };

		// First column is for the MYOB category 1
		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0],
				0, viewer);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				CategoryMappingType p = (CategoryMappingType) element;
				return (p.getMYOBCategory() == null) ? null : p.getMYOBCategory();
			}
		});

		new ColumnViewerSorter(viewer, col) {

			@Override
			protected int doCompare(Viewer viewer, Object e1, Object e2) {
				CategoryMappingType b1 = (CategoryMappingType) e1;
				CategoryMappingType b2 = (CategoryMappingType) e2;
				return b1.getMYOBCategory().compareTo(b2.getMYOBCategory());
			}

		};

		// Second column is for the Web site category name to map to.
		col = createTableViewerColumn(titles[1], bounds[1], 1, viewer);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				CategoryMappingType p = (CategoryMappingType) element;
				return p.getWebSiteCategoryName();
			}
		});

		new ColumnViewerSorter(viewer, col) {

			@Override
			protected int doCompare(Viewer viewer, Object e1, Object e2) {
				CategoryMappingType b1 = (CategoryMappingType) e1;
				CategoryMappingType b2 = (CategoryMappingType) e2;
				String b1Name = b1.getWebSiteCategoryName();
				String b2Name = b2.getWebSiteCategoryName();
				
				if (b1Name == null) {
					b1Name = "";
				}
				
				if (b2Name == null) {
					b2Name = "";
				}
				return b1Name.compareTo(b2Name);
			}
		};
		
		// Third column is for the WebSite category ID to map to.
		col = createTableViewerColumn(titles[2], bounds[2], 1, viewer);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				CategoryMappingType p = (CategoryMappingType) element;
				return p.getWebSiteCategoryID()==null?null:p.getWebSiteCategoryID().toString();
			}
		});

		new ColumnViewerSorter(viewer, col) {

			@Override
			protected int doCompare(Viewer viewer, Object e1, Object e2) {
				CategoryMappingType b1 = (CategoryMappingType) e1;
				CategoryMappingType b2 = (CategoryMappingType) e2;
		
				String b1Id = b1.getWebSiteCategoryID()==null?"":b1.getWebSiteCategoryID().toString();
				String b2Id = b2.getWebSiteCategoryID()==null?"":b2.getWebSiteCategoryID().toString();
				
				return b1Id.compareTo(b2Id);
			}
		};
		
		// Fourth column is for the WebSite field to map to.
		col = createTableViewerColumn(titles[3], bounds[3], 1, viewer);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return WebWidgetsUtils.getWebsiteGroup("3");
			}
		});
	}

}

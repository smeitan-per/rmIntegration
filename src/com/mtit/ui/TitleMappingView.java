package com.mtit.ui;

import java.util.Iterator;

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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.mtit.businessRules.TitleMappingType;

import com.hexapixel.articles.paperclips.core.GridPrinter;
import com.hexapixel.articles.paperclips.test.PrintHandler;
import com.hexapixel.articles.paperclips.test.TableWrapper;
import com.mtit.model.TitleMappingModelProvider;
import com.mtit.process.SyncException;

public class TitleMappingView extends BaseView {

	private TableViewer viewer;
	private Table table;
	private Composite composite;
	private Composite parent;

	public TitleMappingView(Composite parent) {
		super(parent);
		this.parent = parent;
	}

	public Composite createTabFolder() {
		composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(new GridLayout(4, false));

		table = new Table(composite, SWT.FULL_SELECTION | SWT.BORDER);

		createViewer(table);
		createButtons(composite);

		return composite;
	}

	private void createButtons(Composite child) {
		Button addButton = new Button(child, SWT.PUSH);
		addButton.setText("Add");

		GridData gridData = new GridData(GridData.VERTICAL_ALIGN_END);
		gridData.horizontalIndent = 6;
		addButton.setLayoutData(gridData);

		addButton.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				WizardDialog wizardDialog = new WizardDialog(parent.getShell(),
						new TitleMappingWizard("NEW", TitleMappingType.Factory
								.newInstance()));
				System.out.println("Clicked ok");
				if (wizardDialog.open() == Window.OK) {
					viewer.refresh();
				}
			}
		});

		Button editButton = new Button(composite, SWT.PUSH);
		editButton.setText("Edit");
		editButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL));

		editButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection iselection = (IStructuredSelection) viewer
						.getSelection();
				TitleMappingType ruleType = (TitleMappingType) iselection
						.getFirstElement();

				WizardDialog wizardDialog = new WizardDialog(parent.getShell(),
						new TitleMappingWizard("EDIT", ruleType));
				if (wizardDialog.open() == Window.OK) {
					viewer.refresh();
				} else {
				}
			}
		});

		Button deleteButton = new Button(composite, SWT.PUSH);
		deleteButton.setText("Delete");
		deleteButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL));

		deleteButton.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				IStructuredSelection iselection = (IStructuredSelection) viewer
						.getSelection();

				for (Iterator<TitleMappingType> itr = iselection.iterator(); itr
						.hasNext();) {
					TitleMappingType ruleType = (TitleMappingType) itr.next();
					try {
						TitleMappingModelProvider.INSTANCE.delete(ruleType);
					} catch (SyncException e) {
						e.printStackTrace();
					}
				}

				viewer.refresh();
			}
		});

		Button printButton = new Button(composite, SWT.PUSH);
		printButton.setText("Print");
		printButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL));

		printButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				GridPrinter gp = new GridPrinter(new PrintHandler());
				gp.printTable(new TableWrapper(table), false, true, "Print Title Mapping", composite.getShell());

			}

		});

	}

	protected void createViewer(Table table) {
		viewer = new TableViewer(table);
		createColumns(viewer);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setInput(TitleMappingModelProvider.INSTANCE.getTitleMappings());

		// // Layout the viewer
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 4;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		viewer.getControl().setLayoutData(gridData);
	}

	// This will create the columns for the table
	private void createColumns(final TableViewer viewer) {
		String[] titles = { "MYOB Text", "Website Description" };
		int[] bounds = { 100, 300 };

		// First column is for the abbreviation
		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0],
				0, viewer);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TitleMappingType p = (TitleMappingType) element;
				return (p.getMYOBDesc() == null) ? null : p.getMYOBDesc();
			}
		});

		new ColumnViewerSorter(viewer, col) {

			@Override
			protected int doCompare(Viewer viewer, Object e1, Object e2) {
				TitleMappingType b1 = (TitleMappingType) e1;
				TitleMappingType b2 = (TitleMappingType) e2;
				return b1.getMYOBDesc().compareTo(b2.getMYOBDesc());
			}

		};

		// Second column is for the Description to map to.
		col = createTableViewerColumn(titles[1], bounds[1], 1, viewer);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TitleMappingType p = (TitleMappingType) element;
				return p.getWebsiteTitle();
			}
		});

		new ColumnViewerSorter(viewer, col) {

			@Override
			protected int doCompare(Viewer viewer, Object e1, Object e2) {
				TitleMappingType b1 = (TitleMappingType) e1;
				TitleMappingType b2 = (TitleMappingType) e2;
				return b1.getWebsiteTitle().compareTo(b2.getWebsiteTitle());
			}
		};
	}

}

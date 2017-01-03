package com.mtit.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.mtit.businessRules.BusinessRuleType;

import com.hexapixel.articles.paperclips.core.GridPrinter;
import com.hexapixel.articles.paperclips.test.PrintHandler;
import com.hexapixel.articles.paperclips.test.TableWrapper;
import com.mtit.model.BusinessRuleModelProvider;
import com.mtit.process.Controller;
import com.mtit.process.SyncException;

/**
 * SWT View to view list of business rules.
 * 
 * @author Mei
 * 
 */
public class BusinessRulesView extends BaseView {

	private TableViewer viewer;
	private Table table;
	private Composite parent;
	private Composite child;

	public BusinessRulesView(Composite parent) {
		super(parent);
		this.parent = parent;
	}

	/**
	 * Creates and returns Composite to be drawn
	 * 
	 * @return
	 */
	public Composite createTabFolder() {

		child = new Composite(parent, SWT.BORDER);
		child.setLayout(new GridLayout(7, false));

		table = new Table(child, SWT.FULL_SELECTION | SWT.BORDER);

		createViewer(table);
		createButtons(child);

		return child;
	}

	private void createButtons(final Composite composite) {
		
		Button addButton = new Button(composite, SWT.PUSH);
		addButton.setText("Add");
 
		GridData gridData = new GridData(GridData.VERTICAL_ALIGN_END);
		gridData.horizontalIndent = 7;
		addButton.setLayoutData(gridData);
		addButton.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				WizardDialog wizardDialog = new WizardDialog(parent.getShell(), new BusinessRuleWizard("NEW", BusinessRuleType.Factory.newInstance()));
				if (wizardDialog.open() == Window.OK) {
					System.out.println("ok pressed");
					viewer.refresh();
				} else {
					System.out.println("Cancel pressed");
				}
			}
		});
		
		Button editButton = new Button(composite, SWT.PUSH);
		editButton.setText("Edit");
		editButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL));

		editButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection iselection = (IStructuredSelection) viewer.getSelection();
				BusinessRuleType ruleType = (BusinessRuleType) iselection.getFirstElement();
				
				WizardDialog wizardDialog = new WizardDialog(parent.getShell(), new BusinessRuleWizard("EDIT", ruleType));
				if (wizardDialog.open() == Window.OK) {
					System.out.println("ok pressed");
					viewer.refresh();
				} else {
					System.out.println("Cancel pressed");
				}
			}
		});
		
		Button deleteButton = new Button(composite, SWT.PUSH);
		deleteButton.setText("Delete");
		deleteButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL));

		deleteButton.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				IStructuredSelection iselection = (IStructuredSelection) viewer.getSelection();
				
				for (Iterator<BusinessRuleType> itr=iselection.iterator(); itr.hasNext(); ) {
					BusinessRuleType ruleType = (BusinessRuleType) itr.next();
					try {
						BusinessRuleModelProvider.INSTANCE.delete(ruleType);
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
				gp.printTable(new TableWrapper(table), false, true, "Print Test", composite.getShell());

			}

		});

		Button syncButton = new Button(composite, SWT.PUSH);
		syncButton.setText("Synchronize all");
		syncButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL));

		syncButton.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				try {

					ProgressMonitorDialog dialog = new ProgressMonitorDialog(composite.getShell());
					dialog.run(true, false, new IRunnableWithProgress() {
						
						@Override
						public void run(IProgressMonitor arg0) throws InvocationTargetException,
								InterruptedException {
							try {
								Controller.synchronize();
							} catch (SyncException e) {
								throw new InvocationTargetException(e);
							}
						}
					});
					
					MessageBox mb = new MessageBox(parent.getShell());
					mb.setMessage("Synchronize process complete");
					mb.open();
										
				}  catch (InvocationTargetException e) {
					IStatus status = new Status(IStatus.ERROR,"Synchronize error","Synchronize Error",e.getCause());
					ErrorDialog.openError(parent.getShell(), "Synchronize Error Message Dialog", "Encountered error while synchronizing", status);
				} catch (InterruptedException e) {
					IStatus status = new Status(IStatus.ERROR,"Synchronize error","Synchronize Error",e.getCause());
					ErrorDialog.openError(parent.getShell(), "Synchronize Error Message Dialog", "Synchronize process was interrupted", status);
				}
			}
		});

		Button genButton = new Button(composite, SWT.PUSH);
		genButton.setText("Generate File");
		genButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL));

		genButton.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				try {
					ProgressMonitorDialog dialog = new ProgressMonitorDialog(composite.getShell());
					dialog.run(true, false, new IRunnableWithProgress() {

						@Override
						public void run(IProgressMonitor arg0) throws InvocationTargetException,
						InterruptedException {
							try {
								Controller.generateFile();
							} catch (SyncException e) {
								throw new InvocationTargetException(e);
							}
						}
					});
					
					MessageBox mb = new MessageBox(parent.getShell());
					mb.setMessage("File Generation complete");
					mb.open();
					
				}  catch (InvocationTargetException e) {
					
					System.out.println("caught involcation target exception");
					e.printStackTrace();
					IStatus status = new Status(IStatus.ERROR,"Synchronize error","Synchronize Error",e.getCause());
					ErrorDialog.openError(parent.getShell(), "Synchronize Error Message Dialog", "Encountered error while synchronizing", status);
				} catch (InterruptedException e) {
					IStatus status = new Status(IStatus.ERROR,"Synchronize error","Synchronize Error",e.getCause());
					ErrorDialog.openError(parent.getShell(), "Synchronize Error Message Dialog", "Synchronize process was interrupted", status);
				}
			}
		});
	}

	protected TableViewer getViewer() {
		return viewer;
	}

	protected void createViewer(Table table) {
		viewer = new TableViewer(table);
		createColumns(viewer);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setInput(BusinessRuleModelProvider.INSTANCE.getRMRules());

		// // Layout the viewer
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 7;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		viewer.getControl().setLayoutData(gridData);
	}

	// This will create the columns for the table
	private void createColumns(final TableViewer viewer) {
		String[] titles = { "Sequence", "Stock Range Title", "Department",
				"Category 1", "Category 2", "Category 3", "Supplier",
				"Keyword", "Stock Rule" };
		int[] bounds = { 70, 150, 100, 100, 100, 100, 200, 100, 100 };

		// First column is for the sequence number
		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0],
				0, viewer);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				BusinessRuleType p = (BusinessRuleType) element;
				return (p.getSequence() == null) ? null : p.getSequence()
						.toString();
			}
		});

		new ColumnViewerSorter(viewer, col) {

			@Override
			protected int doCompare(Viewer viewer, Object e1, Object e2) {
				BusinessRuleType b1 = (BusinessRuleType) e1;
				BusinessRuleType b2 = (BusinessRuleType) e2;
				return b1.getSequence().compareTo(b2.getSequence());
			}

		};

		// Second column is for the Stock Range Title
		col = createTableViewerColumn(titles[1], bounds[1], 1, viewer);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				BusinessRuleType p = (BusinessRuleType) element;
				return p.getStockRangeTitle();
			}
		});

		new ColumnViewerSorter(viewer, col) {

			@Override
			protected int doCompare(Viewer viewer, Object e1, Object e2) {
				BusinessRuleType b1 = (BusinessRuleType) e1;
				BusinessRuleType b2 = (BusinessRuleType) e2;
				return b1.getStockRangeTitle().compareTo(
						b2.getStockRangeTitle());
			}
		};

		// Now the department
		col = createTableViewerColumn(titles[2], bounds[2], 2, viewer);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				BusinessRuleType p = (BusinessRuleType) element;
				return p.getDepartment();
			}
		});

		new ColumnViewerSorter(viewer, col) {

			@Override
			protected int doCompare(Viewer viewer, Object e1, Object e2) {
				BusinessRuleType b1 = (BusinessRuleType) e1;
				BusinessRuleType b2 = (BusinessRuleType) e2;
				return b1.getDepartment().compareTo(b2.getDepartment());
			}

		};

		// // Now the Category 1
		col = createTableViewerColumn(titles[3], bounds[3], 3, viewer);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				BusinessRuleType rule = (BusinessRuleType) element;
				return rule.getCategory1();
			}

		});

		new ColumnViewerSorter(viewer, col) {

			@Override
			protected int doCompare(Viewer viewer, Object e1, Object e2) {
				BusinessRuleType b1 = (BusinessRuleType) e1;
				BusinessRuleType b2 = (BusinessRuleType) e2;
				return b1.getCategory1().compareTo(b2.getCategory1());
			}
		};

		// // Now the Category 2
		col = createTableViewerColumn(titles[4], bounds[4], 4, viewer);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				BusinessRuleType rule = (BusinessRuleType) element;
				return rule.getCategory2();
			}

		});

		new ColumnViewerSorter(viewer, col) {

			@Override
			protected int doCompare(Viewer viewer, Object e1, Object e2) {
				BusinessRuleType b1 = (BusinessRuleType) e1;
				BusinessRuleType b2 = (BusinessRuleType) e2;
				return b1.getCategory2().compareTo(b2.getCategory2());
			}
		};

		// // Now the Category 3
		col = createTableViewerColumn(titles[5], bounds[5], 5, viewer);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				BusinessRuleType rule = (BusinessRuleType) element;
				return rule.getCategory3();
			}
		});

		new ColumnViewerSorter(viewer, col) {

			@Override
			protected int doCompare(Viewer viewer, Object e1, Object e2) {
				BusinessRuleType b1 = (BusinessRuleType) e1;
				BusinessRuleType b2 = (BusinessRuleType) e2;
				return b1.getCategory3().compareTo(b2.getCategory3());
			}
		};

		// Now the Supplier #
		col = createTableViewerColumn(titles[6], bounds[6], 6, viewer);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				BusinessRuleType rule = (BusinessRuleType) element;
				return rule.getSupplier();
			}
		});

		new ColumnViewerSorter(viewer, col) {

			@Override
			protected int doCompare(Viewer viewer, Object e1, Object e2) {
				BusinessRuleType b1 = (BusinessRuleType) e1;
				BusinessRuleType b2 = (BusinessRuleType) e2;
				return b1.getSupplier().compareTo(b2.getSupplier());
			}
		};

		// Now the Keyword
		col = createTableViewerColumn(titles[7], bounds[7], 7, viewer);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				BusinessRuleType rule = (BusinessRuleType) element;
				return rule.getKeyword();
			}
		});

		new ColumnViewerSorter(viewer, col) {

			@Override
			protected int doCompare(Viewer viewer, Object e1, Object e2) {
				BusinessRuleType b1 = (BusinessRuleType) e1;
				BusinessRuleType b2 = (BusinessRuleType) e2;
				return b1.getKeyword().compareTo(b2.getKeyword());
			}
		};

		// Now the Stock Rule
		col = createTableViewerColumn(titles[8], bounds[8], 8, viewer);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				BusinessRuleType rule = (BusinessRuleType) element;
				return rule.getStockRule();
			}
		});

		new ColumnViewerSorter(viewer, col) {

			@Override
			protected int doCompare(Viewer viewer, Object e1, Object e2) {
				BusinessRuleType b1 = (BusinessRuleType) e1;
				BusinessRuleType b2 = (BusinessRuleType) e2;
				return b1.getStockRule().compareTo(b2.getStockRule());
			}
		};

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(5, false));
		shell.setText("MYOB Interface");

		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		display.dispose();

	}
}

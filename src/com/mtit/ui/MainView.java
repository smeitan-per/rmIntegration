/**
 * 
 */
package com.mtit.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.mtit.entity.IntProperties;

/**
 * @author Mei
 * 
 */
public class MainView {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		IntProperties.propertyPath = args[0];
		
		Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("MYOB Interface");
		shell.setLayout(new FillLayout());
		
		final TabFolder tabFolder = new TabFolder(shell, SWT.BORDER);

		TabItem brItem = new TabItem(tabFolder, SWT.NULL);
		brItem.setText("BusinessRules");

		BusinessRulesView brView = new BusinessRulesView(tabFolder);
		brView.setLayout(new FillLayout());
		brItem.setControl(brView.createTabFolder());

		TabItem titleMappingItem = new TabItem(tabFolder, SWT.NULL);
		titleMappingItem.setText("Title Mapping");

		TitleMappingView titleView = new TitleMappingView(tabFolder);
		titleView.setLayout(new FillLayout());
		titleMappingItem.setControl(titleView.createTabFolder());

		TabItem cat1MappingItem = new TabItem(tabFolder, SWT.NULL);
		cat1MappingItem.setText("Category 1 Mapping");

		Cat1MappingView cat1View = new Cat1MappingView(tabFolder);
		cat1View.setLayout(new FillLayout());
		cat1MappingItem.setControl(cat1View.createTabFolder());

		TabItem cat2MappingItem = new TabItem(tabFolder, SWT.NULL);
		cat2MappingItem.setText("Category 2 Mapping");

		Cat2MappingView cat2View = new Cat2MappingView(tabFolder);
		cat2View.setLayout(new FillLayout());
		cat2MappingItem.setControl(cat2View.createTabFolder());

		TabItem cat3MappingItem = new TabItem(tabFolder, SWT.NULL);
		cat3MappingItem.setText("Category 3 Mapping");

		Cat3MappingView cat3View = new Cat3MappingView(tabFolder);
		cat3View.setLayout(new FillLayout());
		cat3MappingItem.setControl(cat3View.createTabFolder());

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

}

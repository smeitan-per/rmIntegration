package com.mtit.ui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.mtit.businessRules.CategoryMappingType;

import com.mtit.model.Category1MappingModelProvider;
import com.mtit.process.SyncException;

public class Cat1MappingWizard extends Wizard implements IWizard {

	private CategoryMappingType oldMapType;
	private CategoryMappingType mapType;
	private String type;
	
	private final String NEW = "NEW";
	private final String EDIT = "EDIT";
	
	public Cat1MappingWizard(String type, CategoryMappingType mapType) {
		super();
		if (EDIT.equals(type)) {
			this.oldMapType = (CategoryMappingType) mapType.copy();
		}
		this.mapType = mapType;
		this.type = type;
	}
	
	public void addPages() {

		if (this.mapType == null) {
			mapType = CategoryMappingType.Factory.newInstance();
		}
		addPage(new CategoryMappingWizardPage(type, mapType));
	}

	@Override
	public boolean performFinish() {
		
		try {
			if (NEW.equals(type)) {
				Category1MappingModelProvider.INSTANCE.add(mapType);
			} else if (EDIT.equals(type)) {
				Category1MappingModelProvider.INSTANCE.delete(oldMapType);
				Category1MappingModelProvider.INSTANCE.add(mapType);
			}
			return true;
		} catch(SyncException e) {
			MessageDialog.openError(getShell(), "Error:", e.getStackTrace().toString());
			return false;
		}
	}
	

}

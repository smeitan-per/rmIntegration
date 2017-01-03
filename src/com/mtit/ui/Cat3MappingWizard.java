package com.mtit.ui;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.mtit.businessRules.CategoryMappingType;

import com.mtit.model.Category3MappingModelProvider;

public class Cat3MappingWizard extends Wizard implements IWizard {

	private CategoryMappingType oldMapType;
	private CategoryMappingType mapType;
	private String type;
	
	private final String NEW = "NEW";
	private final String EDIT = "EDIT";
	
	public Cat3MappingWizard(String type, CategoryMappingType mapType) {
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
		
		if (NEW.equals(type)) {
			Category3MappingModelProvider.INSTANCE.add(mapType);
		} else if (EDIT.equals(type)) {
			Category3MappingModelProvider.INSTANCE.delete(oldMapType);
			Category3MappingModelProvider.INSTANCE.add(mapType);
		}
		return true;
	}
	

}

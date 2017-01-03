package com.mtit.ui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.mtit.businessRules.TitleMappingType;

import com.mtit.model.TitleMappingModelProvider;
import com.mtit.process.SyncException;

public class TitleMappingWizard extends Wizard implements IWizard {

	private TitleMappingType oldMapType;
	private TitleMappingType mapType;
	private String type;
	
	private final String NEW = "NEW";
	private final String EDIT = "EDIT";
	
	public TitleMappingWizard(String type, TitleMappingType mapType) {
		super();
		if (EDIT.equals(type)) {
			this.oldMapType = (TitleMappingType) mapType.copy();
		}
		this.mapType = mapType;
		this.type = type;
	}
	
	public void addPages() {

		if (this.mapType == null) {
			mapType = TitleMappingType.Factory.newInstance();
		}
		addPage(new TitleMappingWizardPage(type, mapType));
	}

	@Override
	public boolean performFinish() {
		
		System.out.println("perform finish type=" + type);
		
		try {
			if (NEW.equals(type)) {
				TitleMappingModelProvider.INSTANCE.add(mapType);
			} else if (EDIT.equals(type)) {
				TitleMappingModelProvider.INSTANCE.delete(oldMapType);
				TitleMappingModelProvider.INSTANCE.add(mapType);
			}
		} catch(SyncException e) {
			MessageDialog.openError(getShell(), "Error:", e.getStackTrace().toString());
			return false;
		}
		return true;
	}
	

}

package com.mtit.ui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.mtit.businessRules.BusinessRuleType;

import com.mtit.model.BusinessRuleModelProvider;
import com.mtit.process.SyncException;

public class BusinessRuleWizard extends Wizard implements IWizard {

	private BusinessRuleType oldRuleType;
	private BusinessRuleType ruleType;
	private String type;
	
	private final String NEW = "NEW";
	private final String EDIT = "EDIT";
	
	public BusinessRuleWizard(String type, BusinessRuleType ruleType) {
		super();
		if (EDIT.equals(type)) {
			this.oldRuleType = (BusinessRuleType) ruleType.copy();
		}
		this.ruleType = ruleType;
		this.type = type;
	}
	
	public void addPages() {

		if (this.ruleType == null) {
			ruleType = BusinessRuleType.Factory.newInstance();
		}
		addPage(new BusinessRuleWizardPage(type, ruleType));
	}

	@Override
	public boolean performFinish() {
		
		try {
		if (NEW.equals(type)) {
			BusinessRuleModelProvider.INSTANCE.add(ruleType);
		} else if (EDIT.equals(type)) {
			BusinessRuleModelProvider.INSTANCE.delete(oldRuleType);
			BusinessRuleModelProvider.INSTANCE.add(ruleType);
		}
		return true;
		} catch (SyncException e) {
			MessageDialog.openError(getShell(), "Error:", e.getStackTrace().toString());
			return false;
		}
	}
	

}

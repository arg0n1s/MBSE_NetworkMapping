package TGG;

import java.io.File;

import org.apache.log4j.BasicConfigurator;
import org.moflon.tgg.algorithm.synchronization.SynchronizationHelper;

import ReqToImpTransformator.ReqToImpTransformatorPackage;
import ReqToImpTransformator.org.moflon.tie.CustomILPObjectiveProvider;
import ReqToImpTransformator.org.moflon.tie.ReqToImpTransformatorConsistencyCheck;

public class ReqToImpTransformatorCorr extends SynchronizationHelper {

	public ReqToImpTransformatorCorr() {
		super(ReqToImpTransformatorPackage.eINSTANCE, ".");
	}
	
	public void runCorrelation(File srcPath, File trgPath) throws Exception{
		// Set up logging
        BasicConfigurator.configure();
        
        ReqToImpTransformatorConsistencyCheck helper = new ReqToImpTransformatorConsistencyCheck();
        helper.loadSrc("Models/"+srcPath.getName());
		helper.loadTrg("Models/"+trgPath.getName());

		helper.setUserDefiendILPConstraintProvider(new CorrILPConstraintProvider());
		helper.setUserDefiendILPObjectiveProvider(new CustomILPObjectiveProvider());
		helper.createCorrespondences(true);
		
		//src and trg models are modified when preparing deltas.
		//save all files in a separate location
		String srcName = srcPath.getName().substring(0, srcPath.getName().length()-4);
		String trgName = trgPath.getName().substring(0, trgPath.getName().length()-4);
		helper.saveSrc("Correlations/"+srcName+"-"+trgName+"-req.xmi");
		helper.saveTrg("Correlations/"+srcName+"-"+trgName+"-impl.xmi");
		helper.saveCorr("Correlations/"+srcName+"-"+trgName+"-corr.xmi");
		helper.saveConsistencyCheckProtocol("Correlations/"+srcName+"-"+trgName+"-protocol.xmi");
	}

}

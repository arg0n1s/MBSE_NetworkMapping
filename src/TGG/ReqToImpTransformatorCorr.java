package TGG;

import java.io.File;

import org.apache.log4j.BasicConfigurator;
import org.moflon.tgg.algorithm.synchronization.SynchronizationHelper;

import ReqToImpTransformator.ReqToImpTransformatorPackage;
import ReqToImpTransformator.org.moflon.tie.CustomILPObjectiveProvider;

public class ReqToImpTransformatorCorr extends SynchronizationHelper {

	public ReqToImpTransformatorCorr() {
		super(ReqToImpTransformatorPackage.eINSTANCE, ".");
		// Set up logging
        BasicConfigurator.configure();
	}
	
	public void runCorrelation(File srcPath, File trgPath) throws Exception{
		
        this.loadSrc("/Users/Basti/Documents/MBSE/ReqToImpTransformator/instances/src.xmi");
		this.loadTrg("/Users/Basti/Documents/MBSE/ReqToImpTransformator/instances/trg.xmi");

		this.setUserDefiendILPConstraintProvider(new CorrILPConstraintProvider());
		this.setUserDefiendILPObjectiveProvider(new CustomILPObjectiveProvider());
		this.createCorrespondences(true);
		
		//src and trg models are modified when preparing deltas.
		//save all files in a separate location
		String srcName = srcPath.getName().substring(0, srcPath.getName().length()-4);
		String trgName = trgPath.getName().substring(0, trgPath.getName().length()-4);
		this.saveSrc("Correlations/"+srcName+"-"+trgName+"-req.xmi");
		this.saveTrg("Correlations/"+srcName+"-"+trgName+"-impl.xmi");
		this.saveCorr("Correlations/"+srcName+"-"+trgName+"-corr.xmi");
		this.saveConsistencyCheckProtocol("Correlations/"+srcName+"-"+trgName+"-protocol.xmi");
	}

}

package net.sf.cobolToJson.zExample;

import java.io.IOException;

import net.sf.JRecord.Common.IFileStructureConstants;
import net.sf.JRecord.External.CopybookLoader;
import net.sf.JRecord.Option.IReformatFieldNames;
import net.sf.cobolToJson.Cobol2Json;
import net.sf.cobolToJson.zTest.json2cbl.Cbl2JsonCode;

public class TstCbl2JsonAmsPoDownload {

	public static void main(String[] args) throws IOException {
		
		// convert to Json, retain  separate records
		Cobol2Json.newCobol2Json(Cbl2JsonCode.getFullName("cobol/amsPoDownload.cbl"))
				  .setFileOrganization(IFileStructureConstants.IO_BIN_TEXT)
				  .setPrettyPrint(true)
				  .setSplitCopybook(CopybookLoader.SPLIT_01_LEVEL)
				  .setTagFormat(IReformatFieldNames.RO_UNDERSCORE)
				  
					 .setRecordSelection("PO-Record", Cobol2Json.newFieldSelection("Record-Type","H1"))
					 .setRecordSelection("Product-Record", Cobol2Json.newFieldSelection("Record-Type","D1"))
					 .setRecordSelection("Location-Record", Cobol2Json.newFieldSelection("Record-Type","S1"))
					 
			.cobol2json(Cbl2JsonCode.getFullName("Ams_PODownload_20041231.txt"), "/Volumes/BruceMacHD/Temp/amsPoDownload_records.json");
		
		// Convert to Json in a Tree Structure (allow for multiple records / trees
		Cobol2Json.newCobol2Json(Cbl2JsonCode.getFullName("cobol/amsPoDownload.cbl"))
		  .setFileOrganization(IFileStructureConstants.IO_BIN_TEXT)
		  .setSplitCopybook(CopybookLoader.SPLIT_01_LEVEL)
		  .setTagFormat(IReformatFieldNames.RO_UNDERSCORE)
		  
			 .setRecordSelection("PO-Record", Cobol2Json.newFieldSelection("Record-Type","H1"))
			 .setRecordSelection("Product-Record", Cobol2Json.newFieldSelection("Record-Type","D1"))
			 .setRecordSelection("Location-Record", Cobol2Json.newFieldSelection("Record-Type","S1"))
			 
			 .setRecordParent("Product-Record", "PO-Record")
			 .setRecordParent("Location-Record", "Product-Record")
			 
		  .cobol2json(Cbl2JsonCode.getFullName("Ams_PODownload_20041231.txt"), "/Volumes/BruceMacHD/Temp/amsPoDownload_tree.json");
		
		Cobol2Json.newCobol2Json(Cbl2JsonCode.getFullName("cobol/amsPoDownload.cbl"))
		  .setFileOrganization(IFileStructureConstants.IO_BIN_TEXT)
		  .setSplitCopybook(CopybookLoader.SPLIT_01_LEVEL)
		  .setTagFormat(IReformatFieldNames.RO_UNDERSCORE)
		  
			 .setRecordSelection("PO-Record", Cobol2Json.newFieldSelection("Record-Type","H1"))
			 .setRecordSelection("Product-Record", Cobol2Json.newFieldSelection("Record-Type","D1"))
			 .setRecordSelection("Location-Record", Cobol2Json.newFieldSelection("Record-Type","S1"))
			 
			 .setRecordParent("Product-Record", "PO-Record")
			 .setRecordParent("Location-Record", "Product-Record")
			 
			 .setRootRecord("PO-Record")
			 
		  .cobol2json(Cbl2JsonCode.getFullName("Ams_PODownload_20041231.txt"), "/Volumes/BruceMacHD/Temp/amsPoDownload_SingleTree.json");

	}
}

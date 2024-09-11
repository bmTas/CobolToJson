package net.sf.cobolToJson.zExample;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;

import net.sf.JRecord.Common.IFileStructureConstants;
import net.sf.JRecord.External.CopybookLoader;
import net.sf.JRecord.Option.IReformatFieldNames;
import net.sf.cobolToJson.Cobol2Json;
import net.sf.cobolToJson.zTest.json2cbl.Cbl2JsonCode;

public class JsonToCobol02 {

	public static void main(String[] args) throws JsonParseException, IOException {
		
		Cobol2Json.newCobol2Json(Cbl2JsonCode.getFullName("cobol/amsPoDownload.cbl"))
		  .setFileOrganization(IFileStructureConstants.IO_BIN_TEXT)
		  .setSplitCopybook(CopybookLoader.SPLIT_01_LEVEL)
		  .setTagFormat(IReformatFieldNames.RO_UNDERSCORE)
		  
			 .setRecordSelection("PO-Record", Cobol2Json.newFieldSelection("Record-Type","H1"))
			 .setRecordSelection("Product-Record", Cobol2Json.newFieldSelection("Record-Type","D1"))
			 .setRecordSelection("Location-Record", Cobol2Json.newFieldSelection("Record-Type","S1"))
		  .jsonArrayToCobolFile("G:/Temp/amsPoDownload_records.json", "G:/Temp/amsPoDownload_records_json.txt");

	}

}

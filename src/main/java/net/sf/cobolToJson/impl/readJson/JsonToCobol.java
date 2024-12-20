/**
 * 
 */
package net.sf.cobolToJson.impl.readJson;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import net.sf.JRecord.fieldNameConversion.IRenameField;
import net.sf.JRecord.schema.fieldRename.StdFieldRenameItems;

/**
 * @author Bruce Martin
 * 
 * Convert Json to Cobol equivalent
 */
public class JsonToCobol {

	JsonFactory factory = new JsonFactory();
	public void readJson(String jsonFileName, IProcessFields processFields, IRenameField renameField) throws IOException {
		readJson(processFields, new ParserMgr(factory.createParser(new File(jsonFileName)), renameField));
	}
	
	public void readJson(Reader jsonReader, IProcessFields processFields, IRenameField renameField) throws IOException {
		readJson(processFields, new ParserMgr(factory.createParser(jsonReader), renameField));
	}
	
	public void processJson(String json, IProcessFields processFields, IRenameField renameField) throws IOException {
		readJson(processFields, new ParserMgr(factory.createParser(json), renameField));
	}


	private void readJson(IProcessFields processFields, ParserMgr parserMgr) throws IOException {
		ArrayMgr arrayMgr = new ArrayMgr(parserMgr, processFields.isSingleObject());
		int objIdx = 0;
	    //JsonToken jsonToken = parser.nextToken();
		
		if (parserMgr.moreData()){
			parserMgr.nextToken();
			while(parserMgr.moreData()){
				
				switch (parserMgr.jsonToken)  {
				case START_OBJECT:	
					objIdx++;
					arrayMgr.startObj(objIdx);
					break;
				case END_OBJECT:
					if (--objIdx < 0) {
						objIdx = 0;
					}
					
					processFields.endObject(objIdx , arrayMgr.isFirstArray(objIdx), arrayMgr.getFieldName());
					break;
				case START_ARRAY:	arrayMgr.startArray(objIdx);		break;
				case END_ARRAY:		arrayMgr.endArray();		break;
				case FIELD_NAME:	parserMgr.setFieldName();	break;
				case VALUE_NUMBER_INT:
					processFields.updateField(arrayMgr.getFieldName(), parserMgr.parser.getValueAsLong());
					break;
				case VALUE_NUMBER_FLOAT:
				case VALUE_FALSE:
				case VALUE_TRUE:
				case VALUE_STRING:
					processFields.updateField(arrayMgr.getFieldName(), parserMgr.parser.getValueAsString());
					break;
				case VALUE_NULL:
					processFields.updateField(arrayMgr.getFieldName(), null);
					break;
//				case VALUE_NUMBER_FLOAT:
//					System.out.print("\t" +parserMgr.fieldName + "=" +parserMgr.parser.getValueAsString());
//					break;
				}

			parserMgr.nextToken();
			}
		}
		processFields.close();
	}

	private static class ParserMgr {
		final JsonParser  parser;
		final IRenameField renameField;
		JsonToken jsonToken;
		String fieldName="";
		
		public ParserMgr(JsonParser parser, IRenameField renameField) {
			super();
			this.parser = parser;
			this.renameField = renameField == null ? StdFieldRenameItems.LEAVE_ASIS : renameField;
		}

		public JsonToken nextToken() throws IOException {
			return (jsonToken = parser.nextToken());
		}
	
		boolean moreData() {
			return !parser.isClosed();
		}
		
		void setFieldName() throws IOException {
			fieldName = parser.getText();
			if (fieldName == null) {
				fieldName = "";
			} 
			fieldName = renameField.toFieldName(fieldName);
		}
	}
	
	private static class ArrayMgr {
		final ParserMgr parserMgr;
		final boolean singleRecord;
//		int idxNum = -1;
//		int[] indexs = new int[20];
//		int[] objIdx = new int[20];
		ArrayList<IndexDtls> indexDtls = new ArrayList<>();
		
		public ArrayMgr(ParserMgr parserMgr,  boolean singleRecord) {
			super();
			this.parserMgr = parserMgr;
			this.singleRecord = singleRecord;
		}
	
		void startArray(int objectIndex) {
			indexDtls.add(new IndexDtls(objectIndex));
		}
		void startObj(int objectIndex) {
			IndexDtls indexDtls = currentIndexDetails();
			if (indexDtls == null) { return; }
			int idxNum = indexNumber();
			
			indexDtls.isObject = true;
			if (idxNum >= 0 && objectIndex == indexDtls.objectIndex+1) {
				indexDtls.index += 1;
			}
		}
		
		boolean isFirstArray(int objectIndex) {
			return indexDtls.size() == 1 && indexDtls.get(0).objectIndex  == objectIndex;
		}
		void endArray() {
			int idxNum = indexNumber();
			if (idxNum >= 0) {
				indexDtls.remove(idxNum);
			}
		}
		
		int indexNumber() {
			return indexDtls.size() -1;
		}
		
		IndexDtls currentIndexDetails() {
			int indexNum = indexNumber();
			if (indexNum < 0) { return null; }
			return indexDtls.get(indexNum);
		}
		
		String getFieldName( ) {
			String fieldName = parserMgr.fieldName;
			int stIdx = singleRecord ? 0 : 1;
			int idxNum = indexNumber();
			
			if (stIdx<= idxNum) {
				StringBuilder b = new StringBuilder(fieldName).append(" (");
				String sep = "";
				
				currentIndexDetails().updateIndexIfNeeded();
				for (int i = stIdx; i <= idxNum; i++) {
					b.append(sep).append(indexDtls.get(i).index);
					sep = ", ";
				}
				fieldName = b.append(")").toString();
			}
			
			return fieldName;
		}
	}

	private static class IndexDtls {
		int index = -1;
		final int objectIndex;
		boolean isObject = false;
		public IndexDtls(int objectIndex) {
			super();
			this.objectIndex = objectIndex;
		}
		
		void updateIndexIfNeeded() {
			if (! isObject) {
				index += 1;
			}
		}
	}
}

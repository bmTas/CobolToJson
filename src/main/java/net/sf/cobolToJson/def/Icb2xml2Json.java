package net.sf.cobolToJson.def;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

import net.sf.JRecord.Details.AbstractLine;
import net.sf.JRecord.Details.RecordDecider;
import net.sf.JRecord.ExternalRecordSelection.ExternalSelection;
import net.sf.JRecord.Option.IRecordPositionOption;
import net.sf.JRecord.def.IO.builders.ISchemaIOBuilder;
import net.sf.JRecord.def.IO.builders.Icb2xmlLoadOptions;
import net.sf.JRecord.schema.IArrayItemCheck;
import net.sf.JRecord.schema.jaxb.interfaces.IFormatField;
import net.sf.JRecord.schema.jaxb.interfaces.IRedefineSelection;
import net.sf.JRecord.schema.jaxb.interfaces.IWriteCheck;

/**
 * Class To convert <i>Cobol Data Files</i> to/from <i>Json Data files</i> using a cb2xml Json-Schema,
 * This class defines a "Builder" interface for loading the schema-data.
 *  
 * @author Bruce Martin
 *
 */
public interface Icb2xml2Json extends Icb2xmlLoadOptions {
	public static final String MAIN_XML_TAG = "CobolData";


	/**
	 * Get the IoBuilder 
	 * @return iO-Builder
	 */
	ISchemaIOBuilder asIOBuilder();

	/**
	 * File Organization or File Structure (e.g. VB, Fixed Width Etc. Use IFileStructureConstants.IO_*
	 * The main options are:<ul>
	 *   <li><b>IFileStructureConstants.IO_DEFAULT</b> - JRecord will decide the actual method based on other values (The Default Value).
	 *   It is generally better to explicitly set the File-Organisation (or file-Structure).
	 *   <li><b>IFileStructureConstants.IO_STANDARD_TEXT_FILE</b> - Standard Windows/*nix/Mac text file using  \n, \n\r etc as a record (or line) delimiter.
	 *   <li><b>IFileStructureConstants.IO_UNICODE_TEXT</b> - Standard Windows/*nix/Mac Unicode / double byte text file using  \n, \n\r etc 
	 *   as a record (or line) delimiter. It ensures record are stored in character format (instead of bytes).
	 *   <li><b>IFileStructureConstants.IO_FIXED_LENGTH</b> - Every Record (or line) is a standard Fixed length based on the Maximum
	 *   schema (LayoutDetail) record length.
	 *   <li><b>IFileStructureConstants.IO_FIXED_LENGTH_CHAR</b> - Fixed length character file (typically used for Fixed-Length unicode files).
	 *   <li><b>IFileStructureConstants.IO_VB</b> - Mainframe VB (Variable Record length file). Records consist of a Record-Length followed by the Record-Data.
	 *   <li><b>IFileStructureConstants.IO_VB_DUMP</b> - Raw Block format of a Mainframe-VB file. You get this format  if you specify RECFM=U when reading it on the mainframe.
	 *   <li><b>IFileStructureConstants.IO_VB_GNU_COBOL</b> - GNU (open-Cobol) VB format.
	 * </ul>
	 *<pre>
     *<b>Example:</b> 
	 *      {@code
     *      AbstractLineReader r = JRecordInterface1.COBOL
     *              .newIOBuilder("file-name")
     *                  .setFileOrganization(IFileStructureConstants.IO_FIXED_LENGTH)
     *}</pre> 
     *
     *
     *<pre>
	 *     <b>Variable Length</b> where the length is before the Record Data (IO_VB*):
	 *     
	 *           &lt;Record-LengthFixed-Sized-record-Data&lt;record-Data&gt;&lt;Record-Length&gt;&lt;record-Data&gt;&lt;Record-Length&gt;&lt;record-Data&gt;
	 *           
	 *          Record Record Data  	         
	 *          Length ----+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----8----+----9
     *              11 = Record 1>
     *              21 = Record 2 --------->
     *              61 = Record 3 ------------------------------------------------->
     *              25 = Record 4 ------------->
	 *           
	 *     <b>Fixed-Length</b> where all records a of a constant fixed Length (IO_FIXED_LENGTH and IO_FIXED_LENGTH_CHAR:
	 *     
	 *          &lt;Fixed-Sized-record-Data&gt;&lt;Fixed-Sized-record-Data&gt;&lt;Fixed-Sized-record-Data&gt;
	 *          
	 *          Fixed length file (Record Length = 15):
	 *          
	 *          ----+----1----+----2----+----3----+----4----+----5----+----6----+----7----+----8----+----9
	 *          = Record 1 ---&gt;= Record 2 ---&gt;= Record 3 ---&gt;= Record 4 ---&gt;= Record 5 ---&gt;
	 *          
	 *     <b>CSV files</b> with \n embedded in Quotes is another variation
	 * </pre>
	 * 
	 * @param fileOrganization File Organization (or File Structure)
	 * 
	 **/	
	public abstract Icb2xml2Json setFileOrganization(int fileOrganization);

	@Override public abstract Icb2xml2Json setSplitCopybook(int splitCopybook);


	/**
	 * @param font the font (or character set) of the File e.g CP037 is US-EBCDIC, CP273 is German EBCDIC
	 */
	public abstract Icb2xml2Json setFont(String font);

	@Override public abstract Icb2xml2Json setRecordSelection(String recordName, ExternalSelection selectionCriteria);

	@Override public abstract Icb2xml2Json setRecordParent(String recordName, String parentName);

	/**
	 * Define the "Tree Root Record"
	 * @param recordName Name of the Tree Root record
	 * @return this
	 */
	public abstract Icb2xml2Json setRootRecord(String recordName);
	
	@Override public abstract Icb2xml2Json setRecordPositionCode(String recordName,
			IRecordPositionOption positionOption);

	@Override public abstract Icb2xml2Json setRecordDecider(RecordDecider recordDecider);

	/**
	 * Cobol is a column-sensitive language; Traditionally columns 1-5 are used for line-numbers (or version comment)
	 * and ignore everything after column 72. This parameter controls which part of the line to use. Supported values:<ul>
	 *   <li><b>Cb2xmlConstants.USE_STANDARD_COLUMNS</b> -  use columns 6-72 (normal format for mainframe copybooks), this is the default.
	 *   <li><b>Cb2xmlConstants.USE_COLS_6_TO_80</b> -  use columns 6-80
	 *   <li><b>Cb2xmlConstants.USE_LONG_LINE</b> -  use columns 6-10000
	 *   <li><b>Cb2xmlConstants.USE_PROPERTIES_FILE</b> -  columns are supplied in cb2xml.properties file.
	 * </ul>
	 * @param copybookFileFormat the copybookFileFormat to set
	 */
	public abstract Icb2xml2Json setCopybookFileFormat(int copybookFileFormat);

	@Override public abstract Icb2xml2Json setInitToSpaces(boolean initToSpaces);

	/**
	 * Setup check on wether to write Array Item.
	 * @param arrayName Array name
	 * @param check check to be performed; predefined checks include<ul>
	 * <li><b>ArrayElementChecks.INSTANCE.newSkipSpaces()</b> Skip array element if it is all spaces
	 * <li><b>ArrayElementChecks.INSTANCE.newStopAtSpaces()</b> Stop processin array elements if the current element spaces
	 * <li><b>ArrayElementChecks.INSTANCE.newSkipSpacesZeros()</b> Skip array element if it is all spaces/zeros
	 * <li><b>ArrayElementChecks.INSTANCE.newSkipLowValues()</b> Skip array element if it is low values
	 * <li><b>ArrayElementChecks.INSTANCE.newIndexCheck(arraySizeVariableName)</b> process array based on the array size
	 * <li>many others
	 * </ul>
	 * @return this Builder
	 */
	public abstract Icb2xml2Json setArrayCheck(String arrayName, IArrayItemCheck check);
	
	/**
	 * Set format field class
	 * @param fieldName field-name to be formated
	 * @param formatField class to format the field
	 * @return this Builder
	 */
	public abstract Icb2xml2Json setFormatField(String fieldName, IFormatField formatField);

	/**
	 * Set class to check if group/field should be written
	 * @param groupName group/field name
	 * @param writeCheck class to do the check
	 * @return this Builder
	 */
	public abstract Icb2xml2Json setWriteCheck(String groupName, IWriteCheck writeCheck);
	
	/**
	 * Set a Redefines Selection class. This class has a method to select which
	 * redefine-group to use
	 * @param groupName group/field name
	 * @param redefineSelection class to select which redefine-item to use
	 * @return builder for more updates
	 */
	public Icb2xml2Json setRedefineSelection(String groupName, IRedefineSelection redefineSelection);

	/**
	 * Setup check on wether to write Array Item.
	 * @param groupNames Cobol-names that identify the Array
	 * @param check check to be performed; predefined checks include<ul>
	 * <li><b>ArrayElementChecks.INSTANCE.newSkipSpaces()</b> Skip array element if it is all spaces
	 * <li><b>ArrayElementChecks.INSTANCE.newStopAtSpaces()</b> Stop processin array elements if the current element spaces
	 * <li><b>ArrayElementChecks.INSTANCE.newSkipSpacesZeros()</b> Skip array element if it is all spaces/zeros
	 * <li><b>ArrayElementChecks.INSTANCE.newSkipLowValues()</b> Skip array element if it is low values
	 * <li><b>ArrayElementChecks.INSTANCE.newIndexCheck(arraySizeVariableName)</b> process array based on the array size
	 * <li>many others
	 * </ul>
	 * @return this Builder
	 */
	public abstract Icb2xml2Json setArrayCheck(List<String> groupNames, IArrayItemCheck check);
	
	/**
	 * Set format field class
	 * @param groupNames Cobol-names that identify the field to be formated
	 * @param formatField class to format the field
	 * @return this Builder
	 */
	public abstract Icb2xml2Json setFormatField(List<String> groupNames, IFormatField formatField);

	/**
	 * Set class to check if group/field should be written
	 * @param groupNames Cobol-names that identify the group/field to be checked
	 * @param writeCheck class to do the check
	 * @return this Builder
	 */
	public abstract Icb2xml2Json setWriteCheck(List<String> groupNames, IWriteCheck writeCheck);
	
	/**
	 * Set a Redefines Selection class. This class has a method to select which
	 * redefine-group to use
	 * @param groupNames Cobol-names that identify the group/field being redefined
	 * @param redefineSelection class to select which redefine-item to use
	 * @return builder for more updates
	 */
	public Icb2xml2Json setRedefineSelection(List<String> groupNames, IRedefineSelection redefineSelection);


	/**
	 * Set The Tag Format  of Json
	 * @param tagFormat How to format Cobol-names as Json-Tags, valuies<ul>
	 * <li><b>Cbl2XmlValues.RO_LEAVE_ASIS</b> (Default) Keep the Cobol variable name
	 * <li><b>Cbl2XmlValues.RO_MINUS_TO_UNDERSCORE</b> Convert Minus (-) to underscore (_) in Cobol name.
	 * Cobol-Var-Name ==&gt; Cobol_Var_Name
	 * <li><b>Cbl2XmlValues.RO_CAMEL_CASE</b> Camel case conversion Cobol-Var-Name ==&gt; cobolVarName
	 * </ul>
	 * @return
	 */
	public abstract Icb2xml2Json setTagFormat(int tagFormat);

	
	
	@Override  public Icb2xml2Json setDropCopybookNameFromFields(boolean dropCopybookNameFromFields);

	/**
	 * Convert Cobol Data File to Json file
	 * 
	 * @param cobolFileName input Cobol-Data file name
	 * @param xmlFileName output Json-Data file name
	 * 
	 * @throws IOException 
	 * @throws JAXBException
	 */
	public void cobol2json(String cobolFileName, String xmlFileName) throws IOException ;
	/**
	 * Write a Coboi Stream data stream to a writer 
	 * @param cobolStream Cobol Data Stream
	 * @param writer where json is written to
	 * @throws IOException
	 */
	void cobol2json(InputStream cobolStream, Writer writer) throws IOException;

	/**
	 * Convert Cobol Data File to Json file
	 * 
	 * @param cobolStream
	 * @param xmlStream
	 * 
	 * @throws IOException
	 * @throws JAXBException
	 */
	public void cobol2json(InputStream cobolStream, OutputStream xmlStream) throws IOException;


	public Icb2xml2Json setPrettyPrint(boolean prettyPrint);

	/**
	 * Whether to add a field name to the main array
	 * @param nameMainArray wether to name the main JSON array
	 * @return
	 */
	public Icb2xml2Json setNameMainArray(boolean nameMainArray);

	/**
	 * Write sample Json from the Cobol copybook
	 * @param fileName filename where the json is to be written
	 * @return builder for more updates
	 * @throws IOException
	 */
	Icb2xml2Json writeSampleCobol2json(String fileName) throws IOException;

	/**
	 * Write sample Json from the Cobol copybook
	 * @param writer Writer where the json is to be written
	 * @return  builder for more updates
	 * @throws IOException
	 */
	Icb2xml2Json writeSampleCobol2json(Writer writer) throws IOException;

	/**
	 * convert Json
	 * @param jsonReader
	 * @return
	 * @throws IOException
	 */
	AbstractLine jsonObjectToCobolLine(Reader jsonReader) throws IOException;

//	/**
//	 * Convert Input Json-Data to Cobol Data-File
//	 * 
//	 * @param xmlFileName Input Json-File name 
//	 * @param cobolFileName Ouput Cobol-Data File name
//	 * 
//	 * @throws IOException
//	 */
//	public void json2Cobol(String xmlFileName, String cobolFileName)
//			throws IOException,  XMLStreamException;
//
//	/**
//	 * Convert a Json-Data in to a Cobol Data 
//	 * @param xmlStream Input Json Data
//	 * @param cobolStream Output Cobol data
//	 * 
//	 * @throws IOException
//	 */
//	public void json2Cobol(InputStream xmlStream, OutputStream cobolStream)
//			throws IOException, 
//			XMLStreamException;
}

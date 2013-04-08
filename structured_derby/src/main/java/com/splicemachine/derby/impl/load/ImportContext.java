package com.splicemachine.derby.impl.load;

import com.google.common.base.Preconditions;
import com.splicemachine.derby.impl.sql.execute.operations.RowSerializer;
import com.splicemachine.derby.utils.StringUtils;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.reference.SQLState;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.hadoop.fs.Path;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Context information for how to import a File into Splice.
 *
 * @author Scott Fines
 * Created: 2/1/13 11:08 AM
 */
public class ImportContext implements Externalizable{
	private static final long serialVersionUID = 3l;
	protected static final String DEFAULT_COLUMN_DELIMITTER = ",";
	protected static final String DEFAULT_STRIP_STRING = "\"";
    
    private String transactionId;
    
	//the path to the file to import
	private Path filePath;
	//the delimiter which separates columns
	private String columnDelimiter;
	/*
	 * Some CSV-files have quotes in all their columns, or some
	 * other kind of string which needs to be stripped before it can be
	 * properly serialized. This is the stripString to use
	 */
	private String stripString;
	/*
	 * The types of each column, as java.sql.Types ints.
	 */
	private int[] columnTypes;
	//the conglom id
	private long tableId;

	/*
	 * It's possible to only import certain columns out of the file.
	 * This bitset indicates which columns to use. If it's null, then all
	 * columns will be imported
	 */
	private FormatableBitSet activeCols;

	/*
	 * Not everyone formats their timestamps the same way. This is so that
	 * we can be told how to format them. null can be specified if your timestamps
	 * are formatted according to the default (yyyy-MM-dd HH:mm:ss[.fffffffff]), or if
	 * there are no timestamps in the file to be imported.
	 */
	private String timestampFormat;
    private FormatableBitSet pkCols;

    public ImportContext(){}

    private ImportContext(String transactionId,
                          Path filePath,
                          long destTableId,
                          String columnDelimiter,
                          String stripString,
                          int [] columnTypes,
                          FormatableBitSet activeCols,
                          FormatableBitSet pkCols,
                          String timestampFormat){
        this.transactionId = transactionId;
		this.filePath = filePath;
		this.columnDelimiter = columnDelimiter!= null?columnDelimiter:DEFAULT_COLUMN_DELIMITTER;
		this.stripString = stripString!=null?stripString:DEFAULT_STRIP_STRING;
		this.columnTypes = columnTypes;
		this.tableId = destTableId;
		this.activeCols = activeCols;
		this.timestampFormat = timestampFormat;
        this.pkCols = pkCols;
	}

	public Path getFilePath() {
		return filePath;
	}

	public String getColumnDelimiter() {
		return columnDelimiter;
	}

	public String getStripString() {
		return stripString;
	}

	public int[] getColumnTypes() {
		return columnTypes;
	}

	public long getTableId() {
		return tableId;
	}

    public String getTableName(){
        return Long.toString(tableId);
    }

	public FormatableBitSet getActiveCols() {
		return activeCols;
	}

	public String getTimestampFormat() {
		return timestampFormat;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeUTF(transactionId);
        out.writeUTF(filePath.toString());
		out.writeLong(tableId);
		out.writeUTF(columnDelimiter);
		out.writeBoolean(stripString!=null);
		if(stripString!=null)
			out.writeUTF(stripString);
		out.writeInt(columnTypes.length);
		for(int colType:columnTypes){
			out.writeInt(colType);
		}
		out.writeBoolean(activeCols!=null);
		if(activeCols!=null)
			out.writeObject(activeCols);
        out.writeBoolean(pkCols!=null);
        if(pkCols!=null)
            out.writeObject(pkCols);
		out.writeBoolean(timestampFormat!=null);
		if(timestampFormat!=null)
			out.writeUTF(timestampFormat);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        transactionId = in.readUTF();
		filePath = new Path(in.readUTF());
		tableId = in.readLong();
		columnDelimiter = in.readUTF();
		if(in.readBoolean())
			stripString = in.readUTF();
		columnTypes = new int[in.readInt()];
		for(int i=0;i<columnTypes.length;i++){
			columnTypes[i] = in.readInt();
		}
		if(in.readBoolean())
			activeCols = (FormatableBitSet) in.readObject();
        if(in.readBoolean())
            pkCols = (FormatableBitSet)in.readObject();
		if(in.readBoolean())
			timestampFormat = in.readUTF();
	}

	@Override
	public String toString() {
		return "ImportContext{" +
                "transactionId=" + transactionId +
				", filePath=" + filePath +
				", columnDelimiter='" + columnDelimiter + '\'' +
				", stripString='" + stripString + '\'' +
				", columnTypes=" + Arrays.toString(columnTypes) +
				", tableId=" + tableId +
				", activeCols=" + activeCols +
				", timestampFormat='" + timestampFormat + '\'' +
				'}';
	}

    public FormatableBitSet getPrimaryKeys() {
        return pkCols;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public static class Builder{
		private Path filePath;
		private Long tableId;
		private String columnDelimiter;
		private String stripString;
		Map<Integer,Integer> indexToTypeMap = new HashMap<Integer, Integer>();
		private FormatableBitSet activeCols;
		private String timestampFormat;
		private int numCols = -1;
        private FormatableBitSet pkCols;
        private String transactionId;

        public Builder path(Path filePath) {
			this.filePath = filePath;
			return this;
		}

		public Builder path(String path){
			this.filePath = new Path(path);
			return this;
		}

		public Builder destinationTable(long tableId) {
			this.tableId = tableId;
			return this;
		}

		public Builder colDelimiter(String columnDelimiter) {
            String colDelim = StringUtils.parseControlCharacters(columnDelimiter);
            if(System.getProperty("line.separator").equals(colDelim)){
                throw new AssertionError("cannot use linebreaks as column separators");
            }

            //ensure that the System
			this.columnDelimiter = colDelim;
			return this;
		}

		public Builder stripCharacters(String stripString) {
			this.stripString = stripString;
			return this;
		}

		public Builder numColumns(int numCols){
			this.numCols = numCols;
			return this;
		}

		public Builder column(int position, int type){
			indexToTypeMap.put(position,type);
			return this;
		}

		public Builder timestampFormat(String timestampFormat) {
			this.timestampFormat = timestampFormat;
			return this;
		}

        public Builder primaryKeys(FormatableBitSet pkCols) {
            this.pkCols = pkCols;
            return this;
        }

        public Builder transactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }
		public ImportContext build() throws StandardException {
			Preconditions.checkNotNull(filePath,"No File specified!");
			Preconditions.checkNotNull(tableId,"No destination table specified!");
			Preconditions.checkNotNull(columnDelimiter,"No column Delimiter specified");

			int[] colTypes = numCols>0?new int[numCols]:new int[indexToTypeMap.size()];

			FormatableBitSet setBits = new FormatableBitSet(colTypes.length);
			boolean isSparse = false;
			for(int i=0;i<colTypes.length;i++){
				Integer next = indexToTypeMap.get(i);
				if(next!=null){
					colTypes[i] = next;
					setBits.set(i);
				}else{
					isSparse=true;
				}
			}
			if(isSparse){
				activeCols = setBits;
			}

            //validate that active cols contains at least the pkCols
            //otherwise, we won't be able to import the data
            if(activeCols!=null&&pkCols!=null){
                for(int pkCol=pkCols.anySetBit();pkCol!=-1;pkCol = pkCols.anySetBit(pkCol)){
                    if(!activeCols.isSet(pkCol))
                        throw StandardException.newException(SQLState.LANG_ADD_PRIMARY_KEY_FAILED1,
                                "Missing primary key in import");
                }
            }

            return new ImportContext(transactionId, filePath,tableId,
                    columnDelimiter,stripString,
                    colTypes,activeCols,pkCols,
                    timestampFormat);
        }
    }
}

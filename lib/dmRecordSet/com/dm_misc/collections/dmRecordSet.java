package com.dm_misc.collections;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfAttr;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class dmRecordSet {
   private ArrayList<IDfAttr> _columnDefs = new ArrayList<>();
   private ArrayList<IDfTypedObject> _rows = new ArrayList<>();
   private IDfTypedObject _currentRow = null;
   private int _rowCount = 0;
   private int _currentRowNumber = -1;
   private int _firstRow = -1;
   private int _lastRow = -1;
   private boolean _bof = true;
   private boolean _eof = true;
   private String _colNamesHash = "";
   private static final String _version = "dmRecordSet v1.2, (c) 2013 MS Roth, http://msroth.wordpress.com";

   public dmRecordSet(IDfCollection col) throws Exception {
      DfLogger.info(dmRecordSet.class, "dmRecordSet v1.2, (c) 2013 MS Roth, http://msroth.wordpress.com", null, null);
      if (col != null && col.getState() != 2) {
         int c = col.getAttrCount();

         for (int i = 0; i < c; i++) {
            this._columnDefs.add(col.getAttr(i));
            this._colNamesHash = this._colNamesHash + col.getAttr(i).getName();
         }

         while (col.next()) {
            this._rows.add(col.getTypedObject());
         }

         this._rowCount = this._rows.size();
         if (this._rowCount > 0) {
            this._bof = true;
            this._eof = false;
            this._firstRow = 0;
            this._lastRow = this._rowCount - 1;
         } else {
            this._bof = true;
            this._eof = true;
            this._firstRow = -1;
            this._lastRow = -1;
         }

         col.close();
      } else {
         throw new Exception("The IDfCollection object is null or in the closed state.");
      }
   }

   public int getRowCount() {
      return this._rowCount;
   }

   public int getColumnCount() {
      return this._columnDefs.size();
   }

   public ArrayList<IDfAttr> getColumnDefs() {
      return this._columnDefs;
   }

   public boolean isBOF() {
      return this._bof;
   }

   public boolean isEOF() {
      return this._eof;
   }

   public boolean isEmpty() {
      return this._rowCount == 0;
   }

   public boolean hasNext() {
      return this._currentRowNumber + 1 <= this._lastRow;
   }

   @Deprecated
   public IDfTypedObject next() {
      IDfTypedObject tObj;
      try {
         tObj = this.getRow(this._currentRowNumber + 1);
      } catch (Exception var3) {
         tObj = null;
      }

      return tObj;
   }

   public IDfTypedObject getNextRow() throws Exception {
      return this.getRow(this._currentRowNumber + 1);
   }

   public boolean hasPrevious() {
      return this._currentRowNumber - 1 >= this._firstRow;
   }

   @Deprecated
   public IDfTypedObject previous() {
      IDfTypedObject tObj;
      try {
         tObj = this.getRow(this._currentRowNumber - 1);
      } catch (Exception var3) {
         tObj = null;
      }

      return tObj;
   }

   public IDfTypedObject getPreviousRow() throws Exception {
      return this.getRow(this._currentRowNumber - 1);
   }

   @Deprecated
   public IDfTypedObject first() {
      return this.getFirstRow();
   }

   public IDfTypedObject getFirstRow() {
      this._currentRowNumber = this._firstRow;
      this._currentRow = this._rows.get(this._currentRowNumber);
      this._eof = false;
      this._bof = true;
      return this._currentRow;
   }

   @Deprecated
   public IDfTypedObject last() {
      return this.getLastRow();
   }

   public IDfTypedObject getLastRow() {
      this._currentRowNumber = this._lastRow;
      this._currentRow = this._rows.get(this._currentRowNumber);
      this._eof = true;
      this._bof = false;
      return this._currentRow;
   }

   public IDfTypedObject getRow(int rowNumber) throws Exception {
      if (rowNumber >= this._firstRow && rowNumber <= this._lastRow) {
         this._currentRowNumber = rowNumber;
         this._currentRow = this._rows.get(this._currentRowNumber);
         this._eof = false;
         this._bof = false;
      } else {
         if (rowNumber < this._firstRow) {
            this._currentRowNumber = this._firstRow;
            this._currentRow = this._rows.get(this._currentRowNumber);
            this._eof = false;
            this._bof = true;
            DfLogger.warn(dmRecordSet.class, String.format("Row %d precedes first row.  dmRecordSet reset to BOF.", rowNumber), null, null);
            throw new Exception(String.format("WARNING: Row %d precedes first row.  dmRecordSet reset to BOF.", rowNumber));
         }

         if (rowNumber > this._lastRow) {
            this._currentRowNumber = this._lastRow;
            this._currentRow = this._rows.get(this._currentRowNumber);
            this._eof = true;
            this._bof = false;
            DfLogger.warn(dmRecordSet.class, String.format("Row %d is beyond last row.  dmRecordSet set to EOF.", rowNumber), null, null);
            throw new Exception(String.format("WARNING: Row %d is beyond last row.  dmRecordSet set to EOF.", rowNumber));
         }
      }

      return this._currentRow;
   }

   @Deprecated
   public IDfTypedObject getRow() {
      return this.getCurrentRow();
   }

   public IDfTypedObject getCurrentRow() {
      return this._currentRow;
   }

   public int getCurrentRowNumber() {
      return this._currentRowNumber;
   }

   @Deprecated
   public void resetBeginning() {
      this.resetToBeginning();
   }

   public void resetToBeginning() {
      this._currentRowNumber = -1;
      this._currentRow = null;
      this._eof = false;
      this._bof = true;
   }

   @Deprecated
   public void resetEnd() {
      this.resetToEnd();
   }

   public void resetToEnd() {
      this._currentRowNumber = this._rowCount;
      this._currentRow = null;
      this._eof = true;
      this._bof = false;
   }

   public void addRow(IDfTypedObject row) throws Exception {
      String rowColNames = "";

      try {
         for (int i = 0; i < row.getAttrCount(); i++) {
            rowColNames = rowColNames + row.getAttr(i).getName();
         }
      } catch (Exception var4) {
         throw var4;
      }

      if (rowColNames.equalsIgnoreCase(this._colNamesHash)) {
         this._rows.add(row);
         this._rowCount = this._rows.size();
         this._lastRow = this._rowCount - 1;
      } else {
         DfLogger.warn(dmRecordSet.class, "Columns for row do not match record set. Row not added.", null, null);
         throw new Exception("Columns for row do not match record set. Row not added.");
      }
   }

   public void addRows(ArrayList<IDfTypedObject> rows) throws Exception {
      try {
         for (IDfTypedObject row : rows) {
            this.addRow(row);
         }
      } catch (Exception var4) {
         DfLogger.warn(dmRecordSet.class, var4.getMessage(), null, null);
         throw new Exception("Could not add rows: " + var4.getMessage());
      }
   }

   public List<IDfTypedObject> getRecordSetAsList() {
      return this._rows;
   }

   public Set<IDfTypedObject> getRecordSetAsSet() {
      return new HashSet<>(this._rows);
   }

   public String getRecordSetInfo() {
      StringBuilder sb = new StringBuilder();
      String[] dataTypes = new String[]{"BOOLEAN", "INTEGER", "STRING", "ID", "TIME", "DOUBLE", "UNDEFINED"};
      sb.append(getVersion());
      sb.append("\n");
      sb.append("---------------------------------------------------------------\n");
      sb.append(String.format("Row count: %d\n", this.getRowCount()));
      sb.append(String.format("Current row: %d\n", this.getCurrentRowNumber()));
      sb.append(String.format("Column count: %d\n", this.getColumnCount()));
      sb.append("Columns:\n");

      for (int i = 0; i < this.getColumnCount(); i++) {
         sb.append(String.format("\t %s (%s)\n", this._columnDefs.get(i).getName(), dataTypes[this._columnDefs.get(i).getDataType()]));
      }

      sb.append(String.format("is EOF: %s\n", Boolean.toString(this.isEOF())));
      sb.append(String.format("is BOF: %s\n", Boolean.toString(this.isBOF())));
      return sb.toString();
   }

   public static String getVersion() {
      return "dmRecordSet v1.2, (c) 2013 MS Roth, http://msroth.wordpress.com";
   }
}

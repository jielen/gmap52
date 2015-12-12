package com.anyi.gp.util;

import java.io.IOException;
import java.io.Writer;

import com.anyi.gp.Datum;

public interface DataOutputHandler { 
	public void printData(Writer out,Datum datum) throws IOException;
	public void printMetaData(Writer out, Datum datum) throws IOException;
	public void printListData(Writer out, Datum datum) throws IOException;
}

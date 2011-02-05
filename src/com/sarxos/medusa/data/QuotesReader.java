package com.sarxos.medusa.data;

import java.net.URI;
import java.util.List;


public interface QuotesReader<T> {

	public List<T> read(URI uri) throws QuotesReaderException;
	
}

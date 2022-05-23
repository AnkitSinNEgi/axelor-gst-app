package com.axelor.app.gst.service;
import com.axelor.gst.app.Sequence;

public interface SequenceService {
	public String getNextNumber(Sequence seq);
	public String getPaddingString(String seq,int padding);
	public String incrementedSequence(Sequence sequence);
	public String getNextSequence(String modelName);
}

package com.axelor.app.gst.service;


import com.axelor.gst.app.Sequence;
import com.axelor.gst.app.repo.SequenceRepository;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

public class SequenceServiceImp implements SequenceService {

	@Inject
	private SequenceRepository seqRepo;
	
	@Override
	public String getNextSequence(String modelName) {
		
		Sequence seq =seqRepo.all().filter("self.model.name=?",modelName).fetchOne();
		
		return incrementedSequence(seq);
	}
	@Override
	public String getNextNumber(Sequence seq) {
		
		String prefix=seq.getPrefix();
		String suffix=seq.getSuffix();
		int padding=seq.getPadding();
		String nextNumber="";
		if(seq.getNextNumber()==null)
		{	
			if(prefix!=null)
				nextNumber=prefix;
			for(int i=0;i<padding;i++) 
			{
				if(i==padding-1)
					nextNumber=nextNumber.concat("1");
				else
					nextNumber=nextNumber.concat("0");
			}
			if(suffix!=null)
				nextNumber=nextNumber.concat(suffix);
		}else{
			
			if(prefix!=null && suffix!=null)
				nextNumber=prefix+getPaddingString(seq.getNextNumber(),seq.getPadding())+suffix;
			else if(prefix==null && suffix!=null)
				nextNumber=getPaddingString(seq.getNextNumber(),seq.getPadding())+suffix;
			else if(suffix==null && prefix!=null)
				nextNumber=prefix+getPaddingString(seq.getNextNumber(),seq.getPadding());
			else
				nextNumber=getPaddingString(seq.getNextNumber(),seq.getPadding());
		}
		return nextNumber;
	}
	@Override
	public String getPaddingString(String seq,int padding) {
		
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<seq.length();i++) {
			if( seq.charAt(i)>='0' && seq.charAt(i)<='9') {
				sb.append(seq.charAt(i));
			}
		}
		int length=sb.length();
		
		if(length>padding) 
				sb.delete(0, length-padding);
		else if(padding>length){			
			for(int j=0;j<padding-length;j++) 
				sb.insert(0,"0");			
		}
		return sb.toString();
	}
	@Transactional
	@Override
	public String incrementedSequence(Sequence sequence) {
		
		String	nextSequence=sequence.getNextNumber();		
		String pad=getPaddingString(nextSequence,sequence.getPadding());		
		String incremented="";		
		String str=Integer.toString(Integer.parseInt(pad)+1);		
		for(int i=0;i<pad.length()-str.length();i++)
			incremented+="0";
		incremented+=str;
		
		if(sequence.getPrefix()!=null && sequence.getSuffix()!=null) 
			incremented=sequence.getPrefix()+incremented+sequence.getSuffix();
		
		else if(sequence.getPrefix()==null && sequence.getSuffix()!=null)
			incremented=incremented+sequence.getSuffix();
		
		else if(sequence.getSuffix()==null && sequence.getPrefix()!=null)
			incremented=sequence.getPrefix()+incremented;
		
		sequence.setNextNumber(incremented);	
		seqRepo.save(sequence);
		return nextSequence;
	}

}

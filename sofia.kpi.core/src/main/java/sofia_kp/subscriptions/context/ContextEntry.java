package sofia_kp.subscriptions.context;



public final class ContextEntry {

	private final String[] data;
	

	public ContextEntry(String [] data){
		if(data == null){
			throw new IllegalArgumentException("Data cannot be null");
		}
		this.data = data;
		
	}
	
	@Override
	public int hashCode() {
		int hash = 17;
        for(String s : getData()){
        	hash = hash * 23 + s.hashCode();
        }
        return hash;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ContextEntry)){
			return false;
		}
		
		boolean result = true;
		ContextEntry other = (ContextEntry) obj;
		if(other.getData().length != this.getData().length){
			result = false;
		}else{
			for(int i=0;i<this.getData().length && result;i++){
				if(!this.getData()[i].equals(other.getData()[i])){
					result = false;
				}
			}
		}
		return result;
	}

	@Override
	public String toString() {
		String result = "";
		for(String s : getData()){
			result+= s + '\t';
		}
		return result;
	}

	public String[] getData() {
		return data;
	}
}

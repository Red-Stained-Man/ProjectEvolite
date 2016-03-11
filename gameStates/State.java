package gameStates;

import renderEngine.Loader;

public abstract class State {
	
	protected GameStates state;
	
	public State(GameStates state, Loader loader){
		this.state = state;
		loop(loader);
	}
	protected abstract void loop(Loader loader);

	public GameStates getState(){
		return state;
	}

	

}

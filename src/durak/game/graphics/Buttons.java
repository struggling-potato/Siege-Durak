package durak.game.graphics;

public enum Buttons {
	BUTTON_START("Начать игру"),
	BUTTON_PASS("Закончить ход"),
	BUTTON_GIVEUP("Взять карты"),
	BUTTONS_EXITGAME("Выйти из игры");

	private final String label;

	Buttons(String label){
		this.label=label;
	}

	public String getLabel(){
		return label;
	}

	public static Buttons getButtonId(String label){
		for (Buttons b: Buttons.values()){
			if (b.label==label){
				return b;
			}
		}
		return null;
	}


}

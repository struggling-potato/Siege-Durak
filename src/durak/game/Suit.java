package durak.game;

public enum Suit implements GetEmoji {
	SUIT_INVALID {
		@Override
		public String getEmoji() {
			return null;
		}
	},
	SUIT_HEARTS {
		public String getEmoji() {
			return "\u2665";
		}
	},
	SUIT_TILES {
		@Override
		public String getEmoji() {
			return "\u2666";
		}
	},
	SUIT_CLOVERS {
		@Override
		public String getEmoji() {
			return "\u2663";
		}
	},
	SUIT_PIKES {
		@Override
		public String getEmoji() {
			return "\u2660";
		}
	}
}

interface GetEmoji {

	String getEmoji();
}

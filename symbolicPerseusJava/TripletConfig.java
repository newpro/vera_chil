class TripletConfig {
		private DD dd1;
		private DD dd2;
		private int[][] config;

		public TripletConfig(DD dd1, DD dd2, int[][] config) {
				this.dd1 = dd1;
				this.dd2 = dd2;
				this.config = config; // Config.clone(config);
		}

		public int hashCode() {
				return dd1.getAddress() + dd2.getAddress() + Config.hashCode(config);
		}

		public boolean equals(Object obj) {
				
				if (obj.getClass() != getClass()) return false;
				TripletConfig triplet = (TripletConfig)obj;

				if (((dd1 == triplet.dd1 && dd2 == triplet.dd2) || 
						 (dd2 == triplet.dd1 && dd1 == triplet.dd2)) && 
						Config.equals(config, triplet.config)) 
						return true;
				else return false;
		}
}

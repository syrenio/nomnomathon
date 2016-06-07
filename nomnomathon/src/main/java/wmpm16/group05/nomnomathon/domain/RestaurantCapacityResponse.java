package wmpm16.group05.nomnomathon.domain;

/**
 * Created by syrenio on 5/4/2016.
 */
public class RestaurantCapacityResponse {
        private boolean capacityAvailable;
        private Long restaurantId;
        private Long requestid;

        public RestaurantCapacityResponse(boolean capacityAvailable, Long restaurantId, Long requestId) {
			super();
			this.capacityAvailable = capacityAvailable;
			this.restaurantId = restaurantId;
			this.requestid = requestId;
		}
        public RestaurantCapacityResponse(Long restaurantId, Long requestid) {
			super();
			this.restaurantId = restaurantId;
			this.requestid = requestid;
		}

		public boolean isCapacityAvailable() {
            return capacityAvailable;
        }

        public void setCapacityAvailable(boolean capacityAvailable) {
            this.capacityAvailable = capacityAvailable;
        }

		public Long getRestaurantId() {
			return restaurantId;
		}

		public void setRestaurantId(Long restaurantId) {
			this.restaurantId = restaurantId;
		}
		public Long getRequestid() {
			return requestid;
		}
		public void setRequestid(Long requestid) {
			this.requestid = requestid;
		}


        
        
}

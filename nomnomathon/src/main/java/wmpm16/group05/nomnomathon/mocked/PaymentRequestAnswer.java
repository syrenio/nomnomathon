package wmpm16.group05.nomnomathon.mocked;


public class PaymentRequestAnswer {
	private Boolean liquid = false;

	public PaymentRequestAnswer(){}

	public PaymentRequestAnswer(Boolean liquid) {
		this.liquid = liquid;
	}

	public Boolean getLiquid() {
		return liquid;
	}

	public void setLiquid(Boolean liquid) {
		this.liquid = liquid;
	}

	@Override
	public String toString() {
		return "PaymentRequestAnswer [liquid=" + liquid + "]";
	}
	
	
}
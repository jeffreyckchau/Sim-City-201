package bank.interfaces;


public interface AnnouncerB {
	public void msgOnTheWay();
	public void msgAddLoanTeller(LoanTeller l);
	public void msgAddClient(BankClient c);
	public void msgLoanComplete();
}

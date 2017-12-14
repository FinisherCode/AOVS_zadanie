package generated.time1513292050307;

public aspect AdvicesHolder {

	public pointcut examplePointcut(int index) : call (* *(..));
	after() : examplePointcut{}
	around() : examlpePointcut{}
	before() : examplePointcut{}

}

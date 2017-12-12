package generated.time1513070474144;
public aspect SecondClass extends SomeClass {
	public pointcut anotherMethod(Saliva saliva, String abraka, Type38 value) : target(copy) && args(..) && (execution(* BookCopy.borrow(..)) || execution(* BookCopy.return(..)));
	public pointcut doMethod(int index);
	after(Subject s) : doMethod(i){}
	before();
}

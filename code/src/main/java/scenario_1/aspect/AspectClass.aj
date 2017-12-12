package scenario_1.aspect;

public aspect AspectClass {

    public pointcut doAfter(): call();

    public before(): execution(doAfter())  {
        System.out.println("Stalo sa omg omg");
    }

    pointcut setter(): target(scenario_1.model.Point) &&
            (call(void setX(int)) ||
                    call(void setY(int)));

    before() : execution(setter()){
        System.out.println("Stalo sa omg omg");
    }
}

package p04_constructor;

/**
 * This aspect will be generated
 */
public aspect UserConstructorAspect {

    before() : preinitialization(p04_constructor.User.new (..)) && !within(UserConstructorAspect) {
        //write your code here
    }
    before() : initialization(p04_constructor.User.new (..)) && !within(UserConstructorAspect) {
        //write your code here
    }
    before() : call(p04_constructor.User.new (..)) && !within(UserConstructorAspect) {
        //write your code here
    }
    before() : execution(p04_constructor.User.new (..)) && !within(UserConstructorAspect) {
        //write your code here
    }

}

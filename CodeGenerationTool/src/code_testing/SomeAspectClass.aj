package code_testing;

public aspect SomeAspectClass {

    public pointcut doMethod(): call(* User.*());

    after(): doMethod() {
        System.out.println("som v pointcuteeee");
    }

    after(): call(* User.*()) {
        //write your code here
        System.out.println("asdasd");
    }
}

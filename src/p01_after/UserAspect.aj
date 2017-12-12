package p01_after;

/**
 * This aspect will be generated
 */
public aspect UserAspect {

    public pointcut doMethod(): call(* p01_after.User.*());

    after(): doMethod() {
        System.out.println("som v pointcute");
    }

    after(): call(* p01_after.User.*()) {
        //write your code here
        System.out.println("sadasd");
    }



}

package p02_before;

/**
 * This aspect will be generated
 */
public aspect UserAspect {

    before(): call(* p02_before.User.*()) {
        //write your code here
        System.out.println("ASdasdasd");
    }
}

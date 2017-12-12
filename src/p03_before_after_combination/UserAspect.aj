package p03_before_after_combination;

/**
 * This aspect will be generated
 */
public aspect UserAspect {

    before(): call(* p03_before_after_combination.User.*()) {
        //write your code here
    }

    after(): call(* p03_before_after_combination.User.*()) {
        //write your code here
    }
}

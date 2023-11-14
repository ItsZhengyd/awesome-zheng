//package org.example.config;
//
//
//import org.springframework.security.access.AccessDecisionVoter;
//import org.springframework.security.access.ConfigAttribute;
//import org.springframework.security.core.Authentication;
//
//import java.util.Collection;
//
//public class MyAccessDecisionVoter implements AccessDecisionVoter<MyUser> {
//    @Override
//    public boolean supports(ConfigAttribute attribute) {
//        return true;
//    }
//
//    @Override
//    public boolean supports(Class<?> clazz) {
//        return true;
//    }
//
//    @Override
//    public int vote(Authentication authentication, MyUser myUser, Collection<ConfigAttribute> attributes) {
//        if (authentication.getPrincipal() instanceof MyUser) {
//            MyUser user = (MyUser) authentication.getPrincipal();
//            if (user.userName().equals(myUser.userName()) && user.password().equals(myUser.password())) {
//                return 1;
//            }
//        }
//        return 0;
//    }
//}
//
//record MyUser(String userName, String password){}
package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository

public class OrderRepository {
    HashMap<String,Order> ordersMap = new HashMap<>();

    HashMap<String,DeliveryPartner> partnerMap = new HashMap<>();

    HashMap<String,String> orderPartnerMap = new HashMap<>();

    HashMap<String, List<String>> partnerOrdersMap = new HashMap<>();




    public void addOrder(Order order){
        ordersMap.put(order.getId(),order);
    }

    public void addPartner(String partnerId){
        partnerMap.put(partnerId,new DeliveryPartner(partnerId));
    }

    public void addOrderPartnerPair(String orderId,String partnerId){
        if(ordersMap.containsKey(orderId) && partnerMap.containsKey(partnerId)){
            orderPartnerMap.put(orderId,partnerId);
            List<String> currentOrders = new ArrayList<>();

            if(partnerOrdersMap.containsKey(partnerId)){
                currentOrders = partnerOrdersMap.get(partnerId);
            }

            currentOrders.add(orderId);
            partnerOrdersMap.put(partnerId,currentOrders);

            DeliveryPartner deliveryPartner = partnerMap.get(partnerId);

            deliveryPartner.setNumberOfOrders(deliveryPartner.getNumberOfOrders()+1);
        }


    }
    public  Order getOrderById(String orderId) {
        return ordersMap.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId){
        return partnerMap.get(partnerId);
    }

    public int getOrderCountByPartnerId(String partnerId){
        return partnerOrdersMap.get(partnerId).size();
    }
    public List<String> getOrdersByPartnerId(String partnerId){
        return partnerOrdersMap.get(partnerId);
    }

    public List<String> getAllOrders(){
        List<String> ans = new ArrayList<>();

        for(String order : ordersMap.keySet()){
            ans.add(order);
        }

        return ans;
    }

    public int getCountOfUnassignedOrders(){
        return ordersMap.size() - orderPartnerMap.size();
    }

    public int getOrdersLeftAfterGivenTimeByPartnerId(String deliveryTime,String partnerId){

        int count = 0;

        int time = Integer.parseInt(deliveryTime.substring(0,2))*60 + Integer.parseInt(deliveryTime.substring(3));

        List<String> lst = partnerOrdersMap.get(partnerId);

        for(String order : lst){
            int actualTimeRequiredtoDeliverOrder = ordersMap.get(order).getDeliveryTime();

            if(actualTimeRequiredtoDeliverOrder > time){
                count++;
            }
        }
        return count;
    }

    public int getLastDeliveryTimeByPartnerId(String partnerId){
        int maxTime = 0;
        List<String> orders = partnerOrdersMap.get(partnerId);
        for(String orderId: orders){
            int currentTime = ordersMap.get(orderId).getDeliveryTime();
            maxTime = Math.max(maxTime,currentTime);
        }

        return maxTime;
    }

    public void deletePartnerById(String partnerId){
        partnerMap.remove(partnerId);
        List<String> listOfOrders = partnerOrdersMap.get(partnerId);

        partnerOrdersMap.remove(partnerId);

        for(String orderr : listOfOrders){
            orderPartnerMap.remove(orderr);
        }
    }

    public void deleteOrderById(String orderId){
        ordersMap.remove(orderId);

        String partnerId = orderPartnerMap.get(orderId);

        orderPartnerMap.remove(orderId);

        partnerOrdersMap.get(partnerId).remove(orderId);

        partnerMap.get(partnerId).setNumberOfOrders(partnerOrdersMap.get(partnerId).size());
    }
}

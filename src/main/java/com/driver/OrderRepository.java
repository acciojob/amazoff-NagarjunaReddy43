package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class OrderRepository {

    Map<String,Order> ordersDb = new HashMap<>();
    Map<String,DeliveryPartner> deliveryPartnersDb = new HashMap<>();
    Map<String,String> orderPartnerDb = new HashMap<>();
    Map<String, List<String>> partnerOrdersDb = new HashMap<>();

    public void addOrder(Order order){
        ordersDb.put(order.getId(),order);
    }

    public void addPartner(String partnerId){
        deliveryPartnersDb.put(partnerId,new DeliveryPartner(partnerId));
    }

    public void addOrderPartnerPair(String orderId, String partnerId){
        if(ordersDb.containsKey(orderId) && deliveryPartnersDb.containsKey(partnerId)){
            orderPartnerDb.put(orderId,partnerId);

            List<String> currentOrders = new ArrayList<>();

            if(partnerOrdersDb.containsKey(partnerId)){
                currentOrders = partnerOrdersDb.get(partnerId);
            }

            currentOrders.add(orderId);
            partnerOrdersDb.put(partnerId,currentOrders);

            // increase the no of orders of partner
            DeliveryPartner deliveryPartner = deliveryPartnersDb.get(partnerId);
            deliveryPartner.setNumberOfOrders(currentOrders.size());
        }
    }

    public Order getOrderById(String orderId){
        return ordersDb.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId){
        return deliveryPartnersDb.get(partnerId);
    }

    public int getOrderCountByPartnerId(String partnerId){
        return partnerOrdersDb.get(partnerId).size();
    }

    public List<String> getOrdersByPartnerId(String partnerId){
        return partnerOrdersDb.get(partnerId);
    }

    public List<String> getAllOrders(){
        List<String> orders = new ArrayList<>();
        for(String order: ordersDb.keySet()){
            orders.add(order);
        }
        return orders;
    }

    public int getCountOfUnassignedOrders(){
        return ordersDb.size() - orderPartnerDb.size();
    }

    public int getOrdersLeftAfterGivenTimeByPartnerId(int time, String partnerId){
        int count = 0;
        List<String> orders = partnerOrdersDb.get(partnerId);

        for(String orderId: orders){
            int deliveryTime = ordersDb.get(orderId).getDeliveryTime();
            if(deliveryTime>time)
                count++;
        }
        return count;
    }

    public int getLastDeliveryTimeByPartnerId(String parterId){
        int maxTime = 0;
        List<String> orders = partnerOrdersDb.get(parterId);
        for(String orderId: orders){
            int currentTime = ordersDb.get(orderId).getDeliveryTime();
            maxTime = Math.max(maxTime,currentTime);
        }

        return maxTime;
    }

    public void deletePartnerById(String partnerId){

        deliveryPartnersDb.remove(partnerId);

        List<String> listOfOrders = partnerOrdersDb.get(partnerId);
        partnerOrdersDb.remove(partnerId);

        for(String order: listOfOrders){
            orderPartnerDb.remove(order);
        }
    }

    public void deleteOrderById(String orderId){
        ordersDb.remove(orderId);

        String partnerId = orderPartnerDb.get(orderId);
        orderPartnerDb.remove(orderId);

        partnerOrdersDb.get(partnerId).remove(orderId);

        deliveryPartnersDb.get(partnerId).setNumberOfOrders(partnerOrdersDb.get(partnerId).size());
    }

}
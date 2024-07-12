package org.example.bot;

import org.example.DTO.ProductDTO;
import org.example.ReadConfig;
import org.example.entity.Order;
import org.example.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.bot.Constants.START_TEXT;
import static org.example.bot.Constants.CHOOSING_FOODS_OR_DRINKS;
import static org.example.bot.UserState.*;

@Service
public class ReplyServiceImpl implements ReplyService {
    private final Map<Long, UserState> chatStates;
    private final Map<Integer, List<ProductDTO>> tableOrder = new HashMap<>();
    private final Map<Long, Integer> chatTable = new HashMap<>(2);

    private KeyboardFactory keyboardFactory;
    private List<String> order = new ArrayList<>();
    private List<String> positions = new ArrayList<>();
    private List<Short> amounts = new ArrayList<>();
    private OrderRepository orderRepository;

    public ReplyServiceImpl(Map<Long, UserState> chatStates) {
        this.chatStates = chatStates;
    }

    @Autowired
    public void setKeyboardFactory(@Qualifier("fromFile") KeyboardFactory keyboardFactory) {
        this.keyboardFactory = keyboardFactory;
    }

    @Autowired
    public void setOrderRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public SendMessage replyToStart(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(START_TEXT);
        message.setReplyMarkup(keyboardFactory.getKeyboardWithActivities());
        chatStates.put(chatId, UserState.ACTIVE_SELECTION);
        return message;
    }

    @Override
    public SendMessage replyToStop(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("спасибо за общение");
        sendMessage.setReplyMarkup(keyboardFactory.getStart());
        chatStates.clear();
        chatStates.put(chatId, AWAITING_START);
        return sendMessage;
    }


    @Override
    public SendMessage replyForOrderSelection(long chatId, Message message) {
        switch (message.getText()) {
            case Constants.NEW_ORDER -> {
                return getMessageWithFreeTables(chatId);
            }
            case Constants.ADD_TO_ORDER -> {
                return getNotCompletedTables(chatId);
            }
            case Constants.CALCULATE_THE_TABLE -> {
                // занятые столы -> рассчет стола
                return null;
            }
            default -> {
                return unexpectedMessage(chatId);
            }

        }

    }

    private SendMessage getMessageWithFreeTables(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Выберите стол");
        sendMessage.setReplyMarkup(keyboardFactory.getKeyboardFreeTables());
        chatStates.put(chatId, FREE_TABLE_SELECTION);
        return sendMessage;
    }

    private SendMessage getNotCompletedTables(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Выберите стол");
        sendMessage.setReplyMarkup(keyboardFactory.getNotCompletedTables());
        chatStates.put(chatId, NOT_COMPLETED_TABLES_SELECTION);
        return sendMessage;
    }

    @Override
    public SendMessage replyForFreeTable(long chatId, Message message) {
        try {

            orderRepository.save(new Order(Integer.parseInt(message.getText()), false));
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(Constants.SELECTED + message.getText() + "\n" + CHOOSING_FOODS_OR_DRINKS);
            sendMessage.setReplyMarkup(keyboardFactory.getFoodsOrDrinkKeyboardForNewOrder());

            chatStates.put(chatId, FOOD_DRINK_SELECTION);
            tableOrder.put(Integer.parseInt(message.getText()), new ArrayList<>());
            chatTable.put(chatId, Integer.parseInt(message.getText()));

            return sendMessage;
        } catch (NumberFormatException nfe) {
            return unexpectedMessage(chatId);
        }

    }

    @Override
    public SendMessage replyToNotCompletedTable(long chatId, Message message) {
        return null;
    }

    @Override
    public SendMessage replyForFoodDrinkSelection(long chatId, Message message) {
        if (isMessageCorrect(chatId, message)) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            if (message.getText().equals("Еда")) {
                sendMessage.setReplyMarkup(keyboardFactory.getFoodsKeyboard());
            } else {
                if (message.getText().equals("Напитки")) {
                    sendMessage.setReplyMarkup(keyboardFactory.getDrinksKeyboard());
                }
            }
            sendMessage.setText("Выберите позицию");
            chatStates.put(chatId, CHOICE_POSITION);
            return sendMessage;
        } else
            return null;
    }

    @Override
    public SendMessage replyForPosition(long chatId, Message message) {
        if (isMessageCorrect(chatId, message)) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Выбран " + message.getText());
            sendMessage.setReplyMarkup(keyboardFactory.getAmounts());

            chatStates.put(chatId, AMOUNT_SELECTION);

            List<ProductDTO> productsInOrder = tableOrder.get(chatTable.get(chatId));
            productsInOrder.add(new ProductDTO(message.getText()));

            return sendMessage;
            // refactor return
        } else if (message.getText().equals(Constants.RETURN)) {

            if (tableOrder.get(chatTable.get(chatId)).isEmpty()) {
                message.setText(String.valueOf(chatTable.get(chatId)));
                return replyForFreeTable(chatId, message);
            } else {
                List<ProductDTO> productsInOrder = tableOrder.get(chatTable.get(chatId));
                productsInOrder.remove(productsInOrder.size() - 1);
                System.out.println("DEBUG---назад при выборе позиции" + productsInOrder);
                // refactor -> need to calling other method
                return replyStartForOldOrder(chatId, message);
            }
        }
        return unexpectedMessage(chatId);
    }

    @Override
    public SendMessage replyForAmount(long chatId, Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        // refactoring return
        if (message.getText().equals(Constants.RETURN)) {
            List<ProductDTO> productsInOrder = tableOrder.get(chatTable.get(chatId));
            productsInOrder.remove(productsInOrder.size() - 1);

            if (productsInOrder.isEmpty()) {
                message.setText(String.valueOf(chatTable.get(chatId)));
                return replyForFreeTable(chatId, message);
            } else {
                System.out.println("DEBUG---список не пустой");
                // refactor -> need to calling other method
                // condition around chatState
                return replyStartForOldOrder(chatId, message);
            }
        } else {
            try {
                short amount = Short.parseShort(message.getText());
                sendMessage.setText("Добавлено " + amount);

                List<ProductDTO> productsInOrder = tableOrder.get(chatTable.get(chatId));
                ProductDTO lastAddedProduct = productsInOrder.get(productsInOrder.size() - 1);
                lastAddedProduct.setAmount(amount);
                productsInOrder.set(productsInOrder.size() - 1, lastAddedProduct);


                return sendMessage;
            } catch (NumberFormatException nfe) {
                sendMessage.setText("сообщение не понято, напишите число");
                return sendMessage;
            }
        }
    }

    private void setInfoToOrder(Message message) {
        try {
            Integer.parseInt(message.getText());
            String position = order.get(order.size() - 1) + message.getText();
            order.set(order.size() - 1, position);
        } catch (NumberFormatException nfe) {
            order.add(message.getText());
        }
    }

    @Override
    public SendMessage replyStartForOldOrder(long chatId, Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(CHOOSING_FOODS_OR_DRINKS);
        sendMessage.setReplyMarkup(keyboardFactory.getFoodsOrDrinkKeyboardForOldOrder());
        chatStates.put(chatId, UserState.OLD_ORDER_FOOD_DRINK_SELECTION);
        return sendMessage;
    }

    public SendMessage replyForDrinkFoodSelectionForOldOrder(long chatId, Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        switch (message.getText()) {
            case "Подытожить заказ": {
                sendMessage.setText(tableOrder.get(chatTable.get(chatId)).toString());
                return sendMessage;
            }
            case "Еда": {
                sendMessage.setReplyMarkup(keyboardFactory.getFoodsKeyboard());
                sendMessage.setText("Выберите позицию");
                chatStates.put(chatId, CHOICE_POSITION);
                return sendMessage;
            }
            case "Напитки": {
                sendMessage.setReplyMarkup(keyboardFactory.getDrinksKeyboard());
                sendMessage.setText("Выберите позицию");
                chatStates.put(chatId, CHOICE_POSITION);
                return sendMessage;
            }
            case Constants.RETURN: {

                List<ProductDTO> productsInOrder = tableOrder.get(chatTable.get(chatId));
                productsInOrder.remove(productsInOrder.size() - 1);

                return replyForPosition(chatId, message);

            }
            case Constants.HOME: {
                return replyToStart(chatId);
            }
            default: {
                return unexpectedMessage(chatId);
            }
        }
    }

    private List<String> getOrder() {
        List<String> result = new ArrayList<>();
        System.out.println(positions.size() + " " + amounts.size());
        for (int i = 0; i < positions.size(); i++) {
            result.add(positions.get(i) + " " + amounts.get(i));
        }
        return result;
    }

    @Override
    public SendMessage unexpectedMessage(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("не знаю даже что и сказать на такое...");
        return sendMessage;
    }

    private boolean isMessageCorrect(long chatId, Message message) {
        if (chatStates.get(chatId).equals(CHOICE_POSITION)) {
            return isMessageContainsPosition(message);
        }
        return true;
    }

    private boolean isMessageContainsPosition(Message message) {
        return ReadConfig.getDrinks().stream().anyMatch(drink -> drink.getName().equals(message.getText())) ||
                ReadConfig.getFood().stream().anyMatch(food -> food.getName().equals(message.getText()));
    }
}

package org.example.bot;

import org.example.ReadConfig;
import org.example.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.example.bot.Constants.START_TEXT;
import static org.example.bot.Constants.START_TEXT_FOR_OLD_ORDER;
import static org.example.bot.UserState.*;

@Service
public class ReplyServiceImpl implements ReplyService {
    private final Map<Long, UserState> chatStates;
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
        message.setReplyMarkup(keyboardFactory.getKeyboardWithOrders());
        chatStates.put(chatId, UserState.ORDER_SELECTION);
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

            positions.add(message.getText());
            setInfoToOrder(message);

            return sendMessage;
        } else if (message.getText().equals(Constants.RETURN)) {

            if (positions.isEmpty()) {
                return replyToStart(chatId);
            } else {
                return replyStartForOldOrder(chatId);
            }
        }
        return unexpectedMessage(chatId);
    }

    @Override
    public SendMessage replyForAmount(long chatId, Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        if (message.getText().equals(Constants.RETURN)) {
            positions.remove(positions.size() - 1);
            if (positions.isEmpty()) {
                return replyToStart(chatId);
            }
            else {
                return replyStartForOldOrder(chatId);
            }
        } else {
            try {
                short amount = Short.parseShort(message.getText());
                sendMessage.setText("Добавлено " + amount);

                amounts.add(amount);
                setInfoToOrder(message);

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
    public SendMessage replyStartForOldOrder(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(START_TEXT_FOR_OLD_ORDER);
        message.setReplyMarkup(keyboardFactory.getFoodsOrDrinkKeyboardForOldOrder());
        chatStates.put(chatId, UserState.OLD_ORDER_FOOD_DRINK_SELECTION);
        return message;
    }

    public SendMessage replyForDrinkFoodSelectionForOldOrder(long chatId, Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        switch (message.getText()) {
            case "Подытожить заказ": {
                sendMessage.setText(getOrder().toString());
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
                positions.remove(positions.size() - 1);
                amounts.remove(amounts.size() - 1);

                //

                String position = order.get(order.size() - 1);
                StringBuilder sb = new StringBuilder(position);
                sb.deleteCharAt(position.length() - 1);
                position = sb.toString();
                order.set(order.size() - 1, position);

                message.setText(order.get(order.size() - 1));
                order.remove(order.size() - 1);
                return replyForPosition(chatId, message);

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

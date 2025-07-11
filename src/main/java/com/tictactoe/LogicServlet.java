package com.tictactoe;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "LogicServlet", urlPatterns = "/logic")
public class LogicServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Field field = extractField(session);

        int index = getSelectedIndex(req);

        Map<Integer, Sign> fieldData = field.getField();
        if (fieldData.get(index) != Sign.EMPTY) {
            getServletContext().getRequestDispatcher("/index.jsp").forward(req, resp);
            return;
        }

        fieldData.put(index, Sign.CROSS);

        if (checkWin(resp, session, field)) return;

        int indexFieldEmpty = field.getEmptyFieldIndex();

        if (indexFieldEmpty >= 0) {
            fieldData.put(indexFieldEmpty, Sign.NOUGHT);
            if (checkWin(resp, session, field)) return;
        }

        List<Sign> data = field.getFieldData();

        session.setAttribute("field", field);
        session.setAttribute("data", data);

        resp.sendRedirect("/index.jsp");

    }

    private static int getSelectedIndex(HttpServletRequest req) {
        String click = req.getParameter("click");
        boolean isNumeric = click.chars().allMatch(Character::isDigit);
        return isNumeric ? Integer.parseInt(click) : 0;
    }

    private static Field extractField(HttpSession session) {
        Object field = session.getAttribute("field");

        if (Field.class != field.getClass()) {
            session.invalidate();
            throw new RuntimeException("Session is broken, try one more time");
        }
        return (Field) field;
    }

    private boolean checkWin(HttpServletResponse resp, HttpSession session, Field field) throws IOException {
        Sign winner = field.checkWin();
        if (winner == Sign.CROSS || winner == Sign.NOUGHT) {
            session.setAttribute("winner", winner);

            List<Sign> data = field.getFieldData();
            session.setAttribute("data", data);
            resp.sendRedirect("/index.jsp");
            return true;
        }
        return false;
    }
}

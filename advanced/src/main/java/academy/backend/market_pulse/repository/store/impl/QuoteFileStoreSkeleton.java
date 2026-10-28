package academy.backend.market_pulse.repository.store.impl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import academy.backend.market_pulse.factory.InstrumentFactories;
import academy.backend.market_pulse.model.Currency;
import academy.backend.market_pulse.model.Instrument;
import academy.backend.market_pulse.model.Quote;
import academy.backend.market_pulse.repository.store.QuoteFileStore;

public class QuoteFileStoreSkeleton implements QuoteFileStore {

    private static final String SEPARATOR = ";";
    private static final int FIELDS_COUNT = 6;

    @Override
    public void save(Path path, List<Quote> quotes) {
        List<String> lines = new ArrayList<>();
        for (Quote quote : quotes) {
            lines.add(toLine(quote));
        }
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(path, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось сохранить котировки в файл " + path, e);
        }
    }

    @Override
    public List<Quote> load(Path path) {
        List<Quote> quotes = new ArrayList<>();
        if (!Files.exists(path)) {
            return quotes;
        }
        List<String> lines;
        try {
            lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось прочитать котировки из файла " + path, e);
        }
        for (String line : lines) {
            if (!line.isBlank()) {
                quotes.add(fromLine(line));
            }
        }
        return quotes;
    }

    private String toLine(Quote quote) {
        Instrument instrument = quote.getInstrument();
        return instrument.getType() + SEPARATOR
                + instrument.getTicker() + SEPARATOR
                + instrument.getCurrency() + SEPARATOR
                + quote.getPrice().toPlainString() + SEPARATOR
                + quote.getChangePercent().toPlainString() + SEPARATOR
                + instrument.getName();
    }

    private Quote fromLine(String line) {
        String[] parts = line.split(SEPARATOR, FIELDS_COUNT);
        if (parts.length != FIELDS_COUNT) {
            throw new IllegalStateException("Неправильная строка в файле котировок: " + line);
        }
        String type = parts[0];
        String ticker = parts[1];
        Currency currency = Currency.valueOf(parts[2]);
        BigDecimal price = new BigDecimal(parts[3]);
        BigDecimal changePercent = new BigDecimal(parts[4]);
        String name = parts[5];

        Instrument instrument = InstrumentFactories.create(type, ticker, name, currency);
        return new Quote(instrument, price, changePercent);
    }
}

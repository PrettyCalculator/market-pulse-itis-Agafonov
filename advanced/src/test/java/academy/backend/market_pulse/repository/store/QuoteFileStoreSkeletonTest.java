package academy.backend.market_pulse.repository.store;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import academy.backend.market_pulse.factory.InstrumentFactories;
import academy.backend.market_pulse.model.Currency;
import academy.backend.market_pulse.model.Instrument;
import academy.backend.market_pulse.model.Quote;
import academy.backend.market_pulse.repository.store.impl.QuoteFileStoreSkeleton;

/** Проверка моей реализации котировок общим контрактным тестом + пара своих проверок. */
class QuoteFileStoreSkeletonTest extends QuoteFileStoreContractTest {

    @Override
    protected QuoteFileStore createStore() {
        return new QuoteFileStoreSkeleton();
    }

    // В требованиях сказано, что название и изменение тоже должны сохраняться,
    // а контрактный тест их не проверяет — проверяю сам. В названии специально есть ";".
    @Test
    void nameAndChangePercentAreSaved(@TempDir Path tempDir) {
        QuoteFileStore store = createStore();
        Path path = tempDir.resolve("data").resolve("quotes.txt");
        Instrument etf = InstrumentFactories.create("ETF", "TMOS", "Т-Капитал; Индекс МосБиржи", Currency.RUB);

        store.save(path, List.of(new Quote(etf, new BigDecimal("6.95"), new BigDecimal("-2.50"))));
        Quote loaded = store.load(path).get(0);

        assertEquals("ETF", loaded.getInstrument().getType());
        assertEquals("Т-Капитал; Индекс МосБиржи", loaded.getInstrument().getName());
        assertEquals(0, new BigDecimal("-2.50").compareTo(loaded.getChangePercent()));
    }

    @Test
    void saveOverwritesOldFile(@TempDir Path tempDir) {
        QuoteFileStore store = createStore();
        Path path = tempDir.resolve("quotes.txt");
        Instrument sber = InstrumentFactories.create("STOCK", "SBER", "Сбер", Currency.RUB);
        Instrument gazp = InstrumentFactories.create("STOCK", "GAZP", "Газпром", Currency.RUB);

        store.save(path, List.of(new Quote(sber, new BigDecimal("250"), BigDecimal.ZERO),
                new Quote(gazp, new BigDecimal("130"), BigDecimal.ZERO)));
        store.save(path, List.of(new Quote(gazp, new BigDecimal("131"), BigDecimal.ONE)));

        List<Quote> loaded = store.load(path);
        assertEquals(1, loaded.size());
        assertEquals("GAZP", loaded.get(0).getInstrument().getTicker());
    }
}

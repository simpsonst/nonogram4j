// -*- c-basic-offset: 4; indent-tabs-mode: nil -*-

package uk.ac.lancs.nonogram.line.fast;

import java.util.Locale;
import uk.ac.lancs.scc.jardeps.Service;
import uk.ac.lancs.nonogram.line.heuristic.LineHeuristic;
import uk.ac.lancs.nonogram.line.heuristic.LineHeuristicLoader;
import uk.ac.lancs.nonogram.plugin.PluginConfigurationException;

@Service(LineHeuristicLoader.class)
final class FastLineHeuristicLoader implements LineHeuristicLoader {
    @Override
    public LineHeuristic load(String config)
        throws PluginConfigurationException {
        if (config == null) return null;
        if (FastLineHeuristic.HEURISTIC_TYPE.equals(config))
            return FastLineHeuristic.INSTANCE;
        if (config.startsWith(FastLineHeuristic.HEURISTIC_TYPE + ":"))
            return FastLineHeuristic.INSTANCE;
        return null;
    }

    @Override
    public String getSyntax(Locale locale) {
        return FastLineHeuristic.HEURISTIC_TYPE + "[:]";
    }
}

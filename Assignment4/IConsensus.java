public interface IConsensus {
    // Propose value v and return agreed-upon value
    Object decide(Object v);
}

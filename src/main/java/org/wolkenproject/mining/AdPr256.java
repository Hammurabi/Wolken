package org.wolkenproject.mining;

public class AdPr256 {
    /**
     * Algorithm desc:
     *
     * block header, hash must be initialized with zeros.
     * build_cache(header)
     *  loop i = 0 to 128
     *      cache[i-8] = expand_256(header, i)
     *
     * build_data(cache, nonce, target, epoch)
     *  size = target % inital_data_size + epoch_increase * epoch
     *  loop i = 0 to size
     *      data[i:8] = sha256(cache[i%1024] + cache[i/1024])
     *
     * calculate_hash(header, height)
     *  epoch = height / epoch_length
     *  header.hash = empty_array(32)
     *  cache = build_cache(header)
     *  data  = build_data(cache, nonce, target, epoch)
     *  return sha256(data)
     * **/
}
